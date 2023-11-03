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
                        "Owner VARCHAR(64), " +
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
                Common.error(t, "Unable to load player data for " + player.getName());
            }
        });
    }

    private void loadPlayerData(Player player) throws SQLException {
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
            e.printStackTrace();
        }
    }


    private void loadEnchantableItems(Player player) throws SQLException {
        String playerUUID = player.getUniqueId().toString();

        String query = "SELECT ei.*, pit.Slot_In_Inventory FROM Enchantable_Items AS ei " +
                "JOIN player_item_table AS pit ON ei.ITEM_ID = pit.ITEM_ID " +
                "WHERE ei.Owner = ? AND pit.UUID = ?";

        try (PreparedStatement statement = this.getConnection().prepareStatement(query)) {
            statement.setString(1, playerUUID); // This sets the first "?" to the player's UUID
            statement.setString(2, playerUUID); // This sets the second "?" to the player's UUID

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    // Create UUID from ITEM_ID
                    UUID itemId = UUID.fromString(resultSet.getString("ITEM_ID"));
                    String name = resultSet.getString("Name");
                    Material material = Material.getMaterial(resultSet.getString("Material"));
                    Tier tier = Tier.fromInteger(resultSet.getInt("Tier"));
                    int slotInInventory = resultSet.getInt("Slot_In_Inventory");

                    Map<ItemAttribute, Integer> attributes = EnchantableItem.deserializeAttributeTierMap(resultSet.getString("Attributes"));

                    EnchantableItem item = new EnchantableItem(itemId, name, material, attributes, tier);

                    player.getInventory().setItem(slotInInventory, item.compileToItemStack());
                }
            }
        }
    }
}


