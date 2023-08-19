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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MariaDatabase extends SimpleDatabase implements Database {

    @Getter
    private static final MariaDatabase instance = new MariaDatabase();

    public static final EnchantableItem NO_ITEM = null;


    private MariaDatabase() {

    }

    public void connect() {
        String jdbcUrl = "jdbc:mariadb://localhost:3306/minecraftdb";
        String username = "root";
        String password = "password";

        connect(jdbcUrl, username, password);

        Common.runLaterAsync(() -> {
            makeEnchantableTable();
            makePlayerDataTable();
        });
    }

    private void makeEnchantableTable() {
        this.addVariable("table", "Enchantable_Items");
        createTable(TableCreator.of("{table}")
                .addNotNull("UUID", "VARCHAR(64)")
                .add("Name", "TEXT")
                .add("Enchantable_Items", "LONGTEXT")
                .add("Normal_Items", "LONGTEXT")
                .add("Updated", "DATETIME")
                .setPrimaryColumn("UUID")
        );
    }

    private void makePlayerDataTable() {
        this.addVariable("player_data", "Player_Data");
        createTable(TableCreator.of("{player_data}")
                .addNotNull("UUID", "VARCHAR(64)")
                .add("Name", "TEXT")
                .add("Data", "LONGTEXT")
                .add("Updated", "DATETIME")
                .setPrimaryColumn("UUID")
        );
    }

    @Override
    protected void onConnected() {
        Common.broadcast("Connected To MariaDB!");

    }

    public void loadCache(Player player, Consumer<DungeonCache> callThisWhenDataLoaded) {
        Valid.checkSync("Please call loadCache on the main thread.");

        Common.runAsync(() -> {
            try {

                LOAD loadedItems = loadEnchantableItems(player);
                LOAD dataLoaded = loadPlayerData(player);

                if (loadedItems == LOAD.NO_SUCCESS && dataLoaded == LOAD.NO_SUCCESS)
                    callThisWhenDataLoaded.accept(DungeonCache.from(player));

            } catch (Throwable t) {
                Common.error(t, "Unable to load player data for " + player.getName());
            }
        });
    }

    private LOAD loadPlayerData(Player player) throws SQLException {
        DungeonCache cache = DungeonCache.from(player);
        ResultSet playerDataResult = this.query("SELECT * FROM {player_data} WHERE UUID='" + player.getUniqueId() + "'");

        if (playerDataResult == null || !playerDataResult.next())
            return LOAD.NO_SUCCESS;
        else {

            SerializedMap dataMap = SerializedMap.fromJson(playerDataResult.getString("Data"));
            cache.moneyAmount = dataMap.getDouble("Money");

            return LOAD.SUCCESS;
        }

    }

    private LOAD loadEnchantableItems(Player player) throws SQLException {
        ResultSet enchantableResult = this.query("SELECT * FROM {table} WHERE UUID='" + player.getUniqueId() + "'");


        if (enchantableResult == null || !enchantableResult.next())
            return LOAD.NO_SUCCESS;
        else {
            SerializedMap enchantedItemMap = SerializedMap.fromJson(enchantableResult.getString("Enchantable_Items"));
            SerializedMap normalItemMap = SerializedMap.fromJson(enchantableResult.getString("Normal_Items"));

            loadItemsFromMap(player, enchantedItemMap);
            loadItemsFromMap(player, normalItemMap);

            return LOAD.SUCCESS;
        }
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

                this.insert("{player_data}", SerializedMap.ofArray(
                        "UUID", player.getUniqueId(),
                        "Name", player.getName(),
                        "Updated", TimeUtil.toSQLTimestamp(),
                        "Data", cache.toSerializedMap().toJson()
                ));

                Common.runLater(() -> callThisWhenDataSaved.accept(cache));

            } catch (Throwable t) {
                Common.error(t, "Unable to save player data for " + player.getName());
            }
        });
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


    private enum LOAD {
        SUCCESS,
        NO_SUCCESS
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


