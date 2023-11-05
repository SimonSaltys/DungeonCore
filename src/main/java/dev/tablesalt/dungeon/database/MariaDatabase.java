package dev.tablesalt.dungeon.database;

import dev.tablesalt.dungeon.DungeonPlugin;
import dev.tablesalt.dungeon.item.ItemAttribute;
import dev.tablesalt.dungeon.item.Tier;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.database.SimpleDatabase;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

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
                        "FOREIGN KEY (Owner) REFERENCES %s(UUID) ON DELETE CASCADE ON UPDATE NO ACTION, " +
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
                            "UUID VARCHAR(64) NOT NULL, " +
                            "Name TEXT, " +
                            "Data LONGTEXT, " +
                            "Updated DATETIME, " +
                            "PRIMARY KEY (UUID))",
                    playerTable
            );

            query(createPlayerDataTableSQL);
    }

    private void makePlayerItemTable() {
        String createPlayerItemTableSQL = String.format(
                "CREATE TABLE IF NOT EXISTS %s (" +
                        "UUID VARCHAR(64) NOT NULL, " +
                        "ITEM_ID VARCHAR(64) NOT NULL, " +
                        "Slot_In_Inventory TINYINT(3), " +
                        "PRIMARY KEY (UUID, ITEM_ID), " +
                        "FOREIGN KEY (UUID) REFERENCES %s(UUID) ON DELETE CASCADE ON UPDATE NO ACTION, " +
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
                saveItems(player);
                saveData(player);


            } catch (Throwable t) {
                Common.error(t, "Unable to SAVE player data for " + player.getName());
            }
        });
    }

    /*----------------------------------------------------------------*/
    /* HELPER METHODS FOR SAVING AND LOADING */
    /*----------------------------------------------------------------*/

    private void loadPlayerData(Player player)  {
        DungeonCache cache = DungeonCache.from(player);
        String sql = "SELECT * FROM player_data WHERE UUID = ?";

        try (PreparedStatement stmt = this.getConnection().prepareStatement(sql)) {
            stmt.setString(1, player.getUniqueId().toString());

            try (ResultSet playerDataResult = stmt.executeQuery()) {

                if (playerDataResult.next()) {
                    String jsonData = playerDataResult.getString("Data");

                    if (jsonData != null) {
                        SerializedMap dataMap = SerializedMap.fromJson(jsonData);
                        cache.moneyAmount = (dataMap.getDouble("Money"));
                        // You can load other data similarly later...
                    }
                }
            }
        } catch (SQLException e) {
            // Handle exception
           Common.error(e,"Could not load the players " + player.getName() + " data!");
        }
    }


    private void loadEnchantableItems(Player player)  {
        String playerUUID = player.getUniqueId().toString();

        String playerItemsTableQuery = "SELECT ITEM_ID, Slot_In_Inventory FROM " + playerItemTable + " WHERE UUID = ?";

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

                            // Add the item to the player's inventory in the correct slot and update their cache
                            DungeonCache.from(player).addEnchantableItem(item);
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
        String dataSql = "UPDATE " + playerTable + " SET Data = ?, Updated = NOW()";

        try(PreparedStatement statement = this.getConnection().prepareStatement(dataSql)) {
            statement.setString(1,DungeonCache.from(player).toSerializedMap().toJson());
        } catch (SQLException e) {
            Common.error(e,"Could not save players cached data.");
        }
    }


    private void saveItems(Player player) {
        DungeonCache cache = DungeonCache.from(player);

        for (EnchantableItem item : cache.getEnchantableItems()) {
            String sqlItem = "REPLACE INTO " + itemTable + " (ITEM_ID, Tier, Name, Material, Attributes) VALUES (?, ?, ?, ?, ?)";
            try(PreparedStatement saveToItemTableStatement = getConnection().prepareStatement(sqlItem)) {

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
            String sqlPlayerItems = "REPLACE INTO " + playerItemTable + " (UUID, ITEM_ID, Slot_In_Inventory) VALUES (?, ?, ?)";
            try (PreparedStatement stmtPlayerItems = getConnection().prepareStatement(sqlPlayerItems)) {
                stmtPlayerItems.setString(1, player.getUniqueId().toString());
                stmtPlayerItems.setString(2, item.getUuid().toString());
                stmtPlayerItems.setInt(3, EnchantableItem.getSlotInPlayersInventory(item,player)); // Assuming `getInventorySlot` returns the slot index
                stmtPlayerItems.executeUpdate();
            } catch (SQLException e) {
              Common.error(e,"Could not save " + item.getName() + " into the player_items table!!");
            }
        }

    }



}


