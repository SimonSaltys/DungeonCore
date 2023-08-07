package dev.tablesalt.dungeon.database;

import dev.tablesalt.dungeon.util.TBSItemUtil;
import lombok.Getter;
import org.apache.commons.math3.util.Pair;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.TimeUtil;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.database.SimpleDatabase;
import org.mineacademy.fo.model.ConfigSerializable;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class MariaDatabase extends SimpleDatabase implements Database {

    @Getter
    private static final MariaDatabase instance = new MariaDatabase();

    private static final EnchantableItem NO_ITEM = null;


    private MariaDatabase() {
        this.addVariable("table", "Enchantable_Items");
    }

    public void connect() {
        String jdbcUrl = "jdbc:mariadb://localhost:3306/minecraftdb";
        String username = "root";
        String password = "password";

        connect(jdbcUrl, username, password);
    }

    @Override
    protected void onConnected() {
        Common.broadcast("Connected");
        createTable(TableCreator.of("{table}")
                .addNotNull("UUID", "VARCHAR(64)")
                .add("Name", "TEXT")
                .add("Enchantable_Items", "LONGTEXT")
                .add("Updated", "DATETIME")
                .setPrimaryColumn("UUID")
        );
    }

    public void loadCache(Player player, Consumer<DungeonCache> callThisWhenDataLoaded) {
        Valid.checkSync("Please call loadCache on the main thread.");

        Common.runAsync(() -> {
            try {

                ResultSet resultSet = this.query("SELECT * FROM {table} WHERE UUID='" + player.getUniqueId() + "'");

                if (!resultSet.next()) {
                    //we want this on the main thread
                    Common.runLater(() -> callThisWhenDataLoaded.accept(DungeonCache.from(player)));
                }

                UUID uuid = UUID.fromString(resultSet.getString("UUID"));
                String name = resultSet.getString("Name");
                SerializedMap itemsMap = SerializedMap.fromJson(resultSet.getString("Enchantable_Items"));

                loadItemsFromMap(player, itemsMap);


            } catch (Throwable t) {
                Common.error(t, "Unable to load player data for " + player.getName());
            }
        });
    }

    public void saveCache(Player player, Consumer<DungeonCache> callThisWhenDataSaved) {
        Valid.checkSync("Please call loadCache on the main thread.");
        DungeonCache cache = DungeonCache.from(player);


        Common.runAsync(() -> {
            try {
                this.insert("{table}", SerializedMap.ofArray(
                        "UUID", player.getUniqueId(),
                        "Name", player.getName(),
                        "Updated", TimeUtil.toSQLTimestamp(),
                        "Enchantable_Items", playerItemsToMap(player).toJson()
                ));
                Common.runLater(() -> callThisWhenDataSaved.accept(cache));

            } catch (Throwable t) {
                Common.error(t, "Unable to save player data for " + player.getName());
            }
        });
    }

    private void loadItemsFromMap(Player player, SerializedMap itemsMap) {
        DungeonCache cache = DungeonCache.from(player);
        List<ItemSlotPair> itemsAtSlotList = itemsMap.getList("Saved_Items", ItemSlotPair.class);

        for (ItemSlotPair pair : itemsAtSlotList) {
            //add the item to the local plugin cache
            EnchantableItem enchantableItem = pair.getItem();
            cache.addEnchantableItem(enchantableItem);

            Common.broadcast("Loading " + enchantableItem.getName());

            ItemStack compiledItem = enchantableItem.compileToItemStack();

            //if the player is missing the item add it back to their inventory
            for (ItemStack checkingItem : player.getInventory().getContents()) {
                if (checkingItem == null)
                    continue;

                UUID checkedUUID = TBSItemUtil.getItemsUUID(checkingItem);

                if (checkedUUID == null || checkedUUID.equals(enchantableItem.getUuid()))
                    continue;

                player.getInventory().setItem(pair.getSlot(), compiledItem);
                Common.broadcast("Player is missing this item... adding it at" + pair.getSlot());
            }

            Common.broadcast(" ");
        }
    }

    private SerializedMap playerItemsToMap(Player player) {
        DungeonCache cache = DungeonCache.from(player);
        List<ItemSlotPair> itemsAtSlotList = new ArrayList<>();

        for (EnchantableItem item : cache.getEnchantableItems()) {
            Integer slot = item.getSlotInInventory(player);

            if (slot == null)
                continue;

            itemsAtSlotList.add(new ItemSlotPair(item, slot));
        }
        return SerializedMap.ofArray("Saved_Items", itemsAtSlotList);
    }


    private static class ItemSlotPair implements ConfigSerializable {
        private final Pair<EnchantableItem, Integer> pair;

        public ItemSlotPair(EnchantableItem item, Integer slot) {
            pair = new Pair<>(item, slot);
        }

        public EnchantableItem getItem() {
            return pair.getFirst();
        }

        public Integer getSlot() {
            return pair.getSecond();
        }

        @Override
        public SerializedMap serialize() {
            return SerializedMap.ofArray(
                    "Item", getItem(),
                    "Slot", getSlot()
            );
        }

        public static ItemSlotPair deserialize(SerializedMap map) {
            return new ItemSlotPair(map.get("Item", EnchantableItem.class), map.getInteger("Slot"));
        }

    }

}


