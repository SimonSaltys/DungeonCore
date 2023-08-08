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
import java.util.function.Consumer;

public class MariaDatabase extends SimpleDatabase implements Database {

    @Getter
    private static final MariaDatabase instance = new MariaDatabase();

    public static final EnchantableItem NO_ITEM = null;


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
                .add("Normal_Items", "LONGTEXT")
                .add("Updated", "DATETIME")
                .setPrimaryColumn("UUID")
        );
    }

    public void loadCache(Player player, Consumer<DungeonCache> callThisWhenDataLoaded) {
        Valid.checkSync("Please call loadCache on the main thread.");

        Common.runAsync(() -> {
            try {

                ResultSet resultSet = this.query("SELECT * FROM {table} WHERE UUID='" + player.getUniqueId() + "'");

                if (resultSet == null || !resultSet.next()) {
                    //we want this on the main thread
                    Common.runLater(() -> callThisWhenDataLoaded.accept(DungeonCache.from(player)));
                } else {
                    SerializedMap enchantedItemMap = SerializedMap.fromJson(resultSet.getString("Enchantable_Items"));
                    SerializedMap normalItemMap = SerializedMap.fromJson(resultSet.getString("Normal_Items"));

                    loadItemsFromMap(player, enchantedItemMap);
                    loadItemsFromMap(player, normalItemMap);
                }

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
                        "Enchantable_Items", enchantableItemsToMap(player).toJson(),
                        "Normal_Items", normalItemsToMap(player).toJson()
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
            EnchantableItem enchantableItem = pair.getEnchantableItem();

            ItemStack compiledItem;
            if (enchantableItem == null)
                compiledItem = pair.getNormalItem();
            else {
                compiledItem = enchantableItem.compileToItemStack();
                cache.addEnchantableItem(enchantableItem);
            }

            //is there an item that is supposed to be loaded in the enchanter?
            if (pair.getSlot() == Keys.ENCHANTING_MENU_SLOT) {
                cache.setItemInEnchanter(pair.getEnchantableItem());
                continue;
            }

            player.getInventory().setItem(pair.getSlot(), compiledItem);
        }
    }

    private SerializedMap enchantableItemsToMap(Player player) {
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

    private SerializedMap normalItemsToMap(Player player) {
        List<ItemSlotPair> itemsAtSlotList = new ArrayList<>();

        ItemStack[] inventory = player.getInventory().getContents();

        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] == null || TBSItemUtil.isEnchantable(inventory[i]))
                continue;

            itemsAtSlotList.add(new ItemSlotPair(inventory[i], i));
        }

        return SerializedMap.ofArray("Saved_Items", itemsAtSlotList);
    }


    private static class ItemSlotPair implements ConfigSerializable {
        private Pair<EnchantableItem, Integer> enchantablePair;

        private Pair<ItemStack, Integer> normalItemPair;

        public ItemSlotPair(EnchantableItem item, Integer slot) {
            enchantablePair = new Pair<>(item, slot);
        }

        public ItemSlotPair(ItemStack stack, Integer slot) {
            normalItemPair = new Pair<>(stack, slot);
        }

        public EnchantableItem getEnchantableItem() {
            if (enchantablePair == null)
                return null;

            return enchantablePair.getFirst();
        }

        public ItemStack getNormalItem() {
            if (normalItemPair == null)
                return null;

            return normalItemPair.getFirst();
        }

        public int getSlot() {
            if (enchantablePair == null)
                return normalItemPair.getSecond();
            else
                return enchantablePair.getSecond();
        }

        @Override
        public SerializedMap serialize() {
            return SerializedMap.ofArray(
                    "Item", enchantablePair == null ? getNormalItem() : getEnchantableItem(),
                    "Slot", getSlot()
            );
        }

        public static ItemSlotPair deserialize(SerializedMap map) {
            String unparsedData = map.getString("Item");
            if (unparsedData == null)
                return null;

            ItemStack deserializedItem = null;
            if (unparsedData.contains("type"))
                deserializedItem = map.getItemStack("Item");

            if (deserializedItem == null)
                return new ItemSlotPair(map.get("Item", EnchantableItem.class), map.getInteger("Slot"));
            else
                return new ItemSlotPair(deserializedItem, map.getInteger("Slot"));


        }

    }

}


