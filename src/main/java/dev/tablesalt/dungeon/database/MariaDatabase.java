package dev.tablesalt.dungeon.database;

import com.earth2me.essentials.ITarget;
import dev.tablesalt.dungeon.DungeonPlugin;
import dev.tablesalt.dungeon.item.ItemAttribute;
import dev.tablesalt.dungeon.item.Tier;
import dev.tablesalt.dungeon.util.TBSItemUtil;
import io.r2dbc.spi.Parameter;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.database.SimpleDatabase;
import org.mineacademy.fo.remain.Remain;

import java.security.Key;
import java.sql.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * This class is able to load and save relevant data from an off site database.
 * Some examples are the custom items the player has in their inventory,
 * and the amount of money they have
 */
public class MariaDatabase extends SimpleDatabase implements Database {

    @Getter
    private static final MariaDatabase instance = new MariaDatabase();

    public static final EnchantableItem NO_ITEM = null;

    //table names.
    private static final String playerTable = "Player_Data";
    private static final String itemTable = "Enchantable_Items";
    private static final String playerItemTable = "Player_Items";

    private MariaDatabase() {
    }
    /**
     * Attempts to connect to the database
     */
    public void connect() {
        //todo move to a config file, kinda un secure having it here
        String jdbcUrl = "jdbc:mariadb://localhost:3306/minecraftdb";
        String username = "root";
        String password = "644200";

        connect(jdbcUrl, username, password);

        Common.runLaterAsync(0,() -> {
           makePlayerDataTable();
           makeItemTable();
           makePlayerItemTable();
        });
    }

    public void loadForAll() {
        for (Player player : Remain.getOnlinePlayers())
            loadCache(player,cache -> {});
    }

    public void saveForAll() {
        for (Player player: Remain.getOnlinePlayers())
            saveCache(player);
    }

    /**
     * Creates the table that is able to store
     * custom items the player has.
     */
    private void makeItemTable() {
        String createTableSQL = String.format(
                "CREATE TABLE IF NOT EXISTS %s (" +
                        "ITEM_ID VARCHAR(64) NOT NULL, " +
                        "Tier TINYINT(10), " +
                        "Name VARCHAR(32), " +
                        "Material TEXT, " +
                        "Attributes LONGTEXT, " +
                        "PRIMARY KEY (ITEM_ID))",
                itemTable, playerTable
        );
        query(createTableSQL);
    }

    /**
     * Creates the table that is able to store
     * any extra player data we need saved such
     * as their "money".
     */
    private void makePlayerDataTable() {
            String createPlayerDataTableSQL = String.format(
                    "CREATE TABLE IF NOT EXISTS %s (" +
                            "PLAYER_UUID VARCHAR(64) NOT NULL, " +
                            "Name TEXT, " +
                            "Data LONGTEXT, " +
                            "Updated DATETIME, " +
                            "PRIMARY KEY (PLAYER_UUID))",
                    playerTable
            );

            query(createPlayerDataTableSQL);
    }

    private void makePlayerItemTable() {
        String createPlayerItemTableSQL = String.format(
                "CREATE TABLE IF NOT EXISTS %s (" +
                        "PLAYER_UUID VARCHAR(64) NOT NULL, " +
                        "ITEM_ID VARCHAR(64) NOT NULL, " +
                        "Slot_In_Inventory TINYINT(3), " +
                        "PRIMARY KEY (PLAYER_UUID, ITEM_ID), " +
                        "FOREIGN KEY (PLAYER_UUID) REFERENCES %s(PLAYER_UUID) ON DELETE CASCADE ON UPDATE NO ACTION, " +
                        "FOREIGN KEY (ITEM_ID) REFERENCES %s(ITEM_ID) ON DELETE CASCADE ON UPDATE NO ACTION)",
                playerItemTable, playerTable, itemTable
        );

        query(createPlayerItemTableSQL);
    }


    /**
     * Either creates/loads the players cache from the database,
     * so we can use the data in game.
     *
     * @param callThisWhenDataLoaded runs when the cache is loaded with data, can be useful when
     *                               needing to work with the data right away after load
     */
    public void loadCache(Player player, Consumer<DungeonCache> callThisWhenDataLoaded) {
        Valid.checkSync("Please call loadCache on the main thread.");

        Common.runAsync(() -> {
            try {
                loadEnchantableItems(player);
                loadPlayerData(player);

                Common.runLater(() -> callThisWhenDataLoaded.accept(DungeonCache.from(player)));

            } catch (Throwable t) {
                Common.error(t, "Unable to LOAD player data for " + player.getName());
            }
        });
    }

    public void saveCache(Player player) {
        Valid.checkSync("Please call saveCache on the main thread.");

        Common.runAsync(() -> {

            try {
                Common.broadcast("Saving");
                saveData(player);
                saveItems(player);
                removeOwnerships(player);
            } catch (Throwable t) {
                Common.error(t, "Unable to SAVE player data for " + player.getName());
            }
        });
    }
    /*----------------------------------------------------------------*/
    /* HELPER METHODS FOR SAVING AND LOADING */
    /*----------------------------------------------------------------*/

    private void loadPlayerData(Player player) {
        DungeonCache cache = DungeonCache.from(player);
        String sql = "SELECT * FROM player_data WHERE PLAYER_UUID = ?";

        try (PreparedStatement stmt = this.getConnection().prepareStatement(sql)) {
            stmt.setString(1, player.getUniqueId().toString());

            try (ResultSet playerDataResult = stmt.executeQuery()) {

                if (playerDataResult.next()) {
                    String jsonData = playerDataResult.getString("Data");

                    if (jsonData != null) {
                        SerializedMap dataMap = SerializedMap.fromJson(jsonData);

                        //loading money
                        cache.moneyAmount = (dataMap.getDouble("Money"));

                        //loading basic itemstacks (not enchantable, ex: sticks, planks, ect...)
                        HashMap<Integer, ItemStack> normalItems = dataMap.getMap(
                                "Normal_Items", Integer.class,ItemStack.class);

                        for (Map.Entry<Integer, ItemStack> entry : normalItems.entrySet())
                            player.getInventory().setItem(entry.getKey(),entry.getValue());

                        // You can load other data similarly later...
                    }
                }
            }
        } catch (SQLException e) {
            // Handle exception
           Common.error(e,"Could not load the players " + player.getName() + " data!");
        }
    }


    private void loadEnchantableItems(Player player) {
        String playerUUID = player.getUniqueId().toString();
        DungeonCache cache = DungeonCache.from(player);

        String playerItemsTableQuery = "SELECT ITEM_ID, Slot_In_Inventory FROM " + playerItemTable + " WHERE PLAYER_UUID = ?";

        try (PreparedStatement statement = this.getConnection().prepareStatement(playerItemsTableQuery)) {
            statement.setString(1, playerUUID); // This sets the first "?" to the player's UUID

            try (ResultSet playerItemsResult = statement.executeQuery()) {
                while (playerItemsResult.next()) {

                    //Let's get the UUID of the Item and what slot it should be in for the player.
                    UUID itemId = UUID.fromString(playerItemsResult.getString("ITEM_ID"));
                    int slotInInventory = playerItemsResult.getInt("Slot_In_Inventory");


                    //Now lets query the item table with the UUID of the item found to get its data...
                    String itemTableQuery = "SELECT * FROM " + itemTable + " WHERE ITEM_ID = ?";
                    try (PreparedStatement stmtItem = getConnection().prepareStatement(itemTableQuery)) {
                        stmtItem.setString(1, itemId.toString());
                        ResultSet itemResult = stmtItem.executeQuery();

                        //no while here since we are expecting one result.
                        if (itemResult.next()) {
                            // Create an item instance from the ResultSet
                            EnchantableItem item = EnchantableItem.fromResultSet(itemResult);

                            if (slotInInventory == Keys.ENCHANTING_MENU_SLOT)
                                cache.setItemInEnchanter(item);
                            //todo make sure the slot is in the proper range before trying to put it in inventory
                            else
                                player.getInventory().setItem(slotInInventory, item.compileToItemStack());
                        }
                    }
                }
            }
        } catch (SQLException e) {
            Common.error(e,"Could not load items from the database to the player " + player);
        }
    }

    private void saveData(Player player) {

        String dataSql = "REPLACE INTO " + playerTable + " (PLAYER_UUID, Name, Data, Updated) VALUES (?, ?, ?, ?)";

        try(PreparedStatement statement = this.getConnection().prepareStatement(dataSql)) {
            statement.setString(1,player.getUniqueId().toString());
            statement.setString(2,player.getName());
            statement.setString(3,DungeonCache.toSerializedMap(player).toJson());
            statement.setTimestamp(4,new Timestamp(System.currentTimeMillis()));
            statement.executeUpdate();
        } catch (SQLException e) {
            Common.error(e,"Could not save players cached data.");
        }
    }


    private void saveItems(Player player) {
        PlayerInventory inventory = player.getInventory();
        DungeonCache cache = DungeonCache.from(player);

        //save all enchantable items in inventory
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack itemStack = inventory.getItem(i);
            if (itemStack == null)
                continue;

            if (!TBSItemUtil.isEnchantable(itemStack))
                continue;

            saveItem(player,itemStack,i);
        }

        //save the item in the item upgrader, they might of left it in it...
        EnchantableItem itemInEnchanter = cache.getItemInEnchanter();
        if (itemInEnchanter != null)
            saveItem(player, itemInEnchanter, Keys.ENCHANTING_MENU_SLOT);

        //save there stash later
    }

    private void saveItem(Player player, ItemStack itemStack, int slot) {
        EnchantableItem item = EnchantableItem.fromItemStack(itemStack);

        saveItem(player,item,slot);
    }

    private void saveItem(Player player, EnchantableItem item, int slot) {
        String sqlItem = "REPLACE INTO " + itemTable + " (ITEM_ID, Tier, Name, Material, Attributes) VALUES (?, ?, ?, ?, ?)";
        try(PreparedStatement saveToItemTableStatement = getConnection().prepareStatement(sqlItem)) {

            Common.broadcast("Saving item " + item.getFormattedName() + " in slot &a" + slot);

            saveToItemTableStatement.setString(1, item.getUuid().toString());
            saveToItemTableStatement.setInt(2, item.getCurrentTier().getAsInteger());
            saveToItemTableStatement.setString(3, item.getName());
            saveToItemTableStatement.setString(4, item.getMaterial().toString());
            saveToItemTableStatement.setString(5, item.serializeAttributeTierMap()); // Convert attributes to JSON
            saveToItemTableStatement.executeUpdate();

        } catch (SQLException e) {
            Common.error(e,"Could not save item " + item.getName() + " to the items table!!!");
        }


        // Now we only need to update the player_items with the reference
        String sqlPlayerItems = "REPLACE INTO " + playerItemTable + " (PLAYER_UUID, ITEM_ID, Slot_In_Inventory) VALUES (?, ?, ?)";
        try (PreparedStatement stmtPlayerItems = getConnection().prepareStatement(sqlPlayerItems)) {
            stmtPlayerItems.setString(1, player.getUniqueId().toString());
            stmtPlayerItems.setString(2, item.getUuid().toString());
            stmtPlayerItems.setInt(3, slot);
            stmtPlayerItems.executeUpdate();
        } catch (SQLException e) {
            Common.error(e,"Could not save " + item.getName() + " into the player_items table!!");
        }
    }


    //todo move this to bungee cord to deal with. we only want one task of this running for all programs and threads since
    // this is a very expensive operation.
    private void removeItemsWithoutOwners() {
        String sqlRemoveItems = "DELETE FROM " + itemTable + " WHERE ITEM_ID NOT IN " +
                "(SELECT ITEM_ID FROM " + playerItemTable + ")";

        query(sqlRemoveItems);
    }

    private void removeOwnerships(Player player) {
        List<UUID> itemIdsOwnedByPlayer = new ArrayList<>();
        EnchantableItem itemInEnchanter = DungeonCache.from(player).getItemInEnchanter();

        for (ItemStack stack : player.getInventory()) {
            EnchantableItem item = EnchantableItem.fromItemStack(stack);
            if (item == null)
                continue;

            itemIdsOwnedByPlayer.add(item.getUuid());
        }

        if (itemInEnchanter != null)
            itemIdsOwnedByPlayer.add(itemInEnchanter.getUuid());

        String sql;
        if (itemIdsOwnedByPlayer.isEmpty()) {
            // If no items in inventory, then we assume we need to delete all items for this player.
            sql = "DELETE FROM " + playerItemTable + " WHERE PLAYER_UUID = ?";
        } else {
            String placeholders = String.join(",", Collections.nCopies(itemIdsOwnedByPlayer.size(), "?"));
            sql = "DELETE FROM " + playerItemTable + " WHERE ITEM_ID NOT IN (" + placeholders + ") AND PLAYER_UUID = ?";
        }

        try(PreparedStatement stmtRemoveOwnership = getConnection().prepareStatement(sql)) {
            int index = 1;

            if (!itemIdsOwnedByPlayer.isEmpty()) {
                for (UUID itemId : itemIdsOwnedByPlayer) {
                    stmtRemoveOwnership.setString(index++, itemId.toString());
                }
            }

            // Assuming the player ID is a String or similar; you may need to adjust based on your schema.
            stmtRemoveOwnership.setString(index, player.getUniqueId().toString());

            int deletedRows = stmtRemoveOwnership.executeUpdate();
            Common.broadcast("&cDELETED ROWS &b#" + deletedRows);

        } catch (SQLException e) {
            Common.error(e,"Could not remove ownerships for player " + player.getName());
        }
    }



}


