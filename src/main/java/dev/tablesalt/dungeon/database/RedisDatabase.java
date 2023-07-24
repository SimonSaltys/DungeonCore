package dev.tablesalt.dungeon.database;


import dev.tablesalt.dungeon.DungeonPlugin;
import dev.tablesalt.dungeon.menu.enchanting.EnchantingMenu;
import dev.tablesalt.dungeon.util.MessageUtil;
import dev.tablesalt.dungeon.util.PlayerUtil;
import dev.tablesalt.dungeon.util.TBSItemUtil;
import dev.tablesalt.gamelib.players.PlayerCache;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.remain.Remain;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;


public class RedisDatabase {

    @Getter
    private static final RedisDatabase instance = new RedisDatabase();

    private static final String PLAYER_ITEMS = "Player_Inventories:";

    private static final String PLAYER_MONEY = "Player_Money:";

    private JedisPool pool;

    private Jedis jedis;

    private RedisDatabase() {
    }

    public void connect() {
        if (isConnected())
            return;

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(32);

        pool = new JedisPool(poolConfig, "localhost", 6379, 10000);

        jedis = pool.getResource();

        //Give sometime to make sure database is connected
        Common.runLater(80, this::scheduleAsyncSaveTask);

        Common.log(isConnected() ? MessageUtil.makeSuccessful("Connected to redis") : MessageUtil.makeError("Could not connect to redis"));
    }

    public boolean isConnected() {
        return pool != null && jedis.isConnected();
    }

    private void scheduleAsyncSaveTask() {
        if (!isConnected())
            return;

        Bukkit.getScheduler().scheduleSyncRepeatingTask(DungeonPlugin.getInstance(), () -> {
            for (Player player : Remain.getOnlinePlayers())
                if (!PlayerCache.from(player).getGameIdentifier().hasGame())
                    saveItems(player);

            Common.log(MessageUtil.makeInfo("Saving inventories of all players currently online."));

        }, 0, 20L * 300);
    }

    public void saveForAll() {
        for (Player player : Remain.getOnlinePlayers())
            RedisDatabase.getInstance().save(player);

    }

    public void loadForAll() {
        for (Player player : Remain.getOnlinePlayers()) {
            RedisDatabase.getInstance().load(player);
        }
    }

    public void save(Player player) {
        if (!isConnected())
            return;

        saveMoney(player);
        saveItems(player);
    }

    public void load(Player player) {
        if (!isConnected())
            return;

        loadMoney(player);

        try {
            loadItems(player);
        } catch (IOException e) {
            Common.throwError(e, "Could not parse player's " + player.getName() + " inventory from a base64 string");
        }
    }

    private void saveMoney(Player player) {
        DungeonCache cache = DungeonCache.from(player);

        Common.runAsync(() -> jedis.set(PLAYER_MONEY + player.getUniqueId(), cache.getMoney() + ""));
    }

    private void loadMoney(Player player) {
        DungeonCache cache = DungeonCache.from(player);
        String moneyString = jedis.get(PLAYER_MONEY + player.getUniqueId());

        if (moneyString != null) {
            try {
                int money = Integer.parseInt(moneyString);
                cache.setMoney(money);
            } catch (NumberFormatException e) {
                Common.throwError(e, "Could not load money for player " + player.getName());
            }
        } else
            cache.setMoney(0);
    }


    private void saveItems(Player player) {

        PlayerInventory inventory = player.getInventory();
        PlayerCache cache = PlayerCache.from(player);
        DungeonCache dungeonCache = DungeonCache.from(player);

        ItemStack itemLeftInEnchanter = cache.getTagger().getPlayerTag(EnchantingMenu.ITEM_STILL_IN_ENCHANTER);

        SerializedMap inventoriesMap = SerializedMap.ofArray(
                "Main_Inventory", BukkitSerialization.itemStackArrayToBase64(PlayerUtil.getAllItemsExcludingArmor(player)),
                "Armor_Contents", BukkitSerialization.itemStackArrayToBase64(inventory.getArmorContents()),
                "Off_Hand_Contents", BukkitSerialization.itemStackToBase64(inventory.getItemInOffHand()),
                "Enchanting_Table_Contents", itemLeftInEnchanter != null ? BukkitSerialization.itemStackToBase64(itemLeftInEnchanter) : null);

        dungeonCache.removeOldEnchantableItems(player);

        Common.runAsync(() -> jedis.set(PLAYER_ITEMS + player.getUniqueId(), inventoriesMap.toJson()));
    }

    private void loadItems(Player player) throws IOException {
        SerializedMap map = SerializedMap.fromJson(jedis.get(PLAYER_ITEMS + player.getUniqueId()));
        PlayerCache cache = PlayerCache.from(player);
        DungeonCache dungeonCache = DungeonCache.from(player);

        PlayerInventory currentInventory = player.getInventory();
        ItemStack[] savedInventory = BukkitSerialization.itemStackArrayFromBase64(map.get("Main_Inventory", String.class));
        ItemStack[] savedArmor = BukkitSerialization.itemStackArrayFromBase64(map.get("Armor_Contents", String.class));
        ItemStack savedOffHand = BukkitSerialization.itemStackFromBase64(map.get("Off_Hand_Contents", String.class));

       loadItemLeftInEnchanter(cache,map);

        //load main inventory
        for (int i = 0; i < 36; i++)
            if (currentInventory.getItem(i) != savedInventory[i])
                currentInventory.setItem(i, savedInventory[i]);

        //load offhand
        if (!savedOffHand.equals(currentInventory.getItemInOffHand()))
            currentInventory.setItem(EquipmentSlot.OFF_HAND, savedOffHand);

        //load armor
        for (int i = 0; i < 4; i++) {
            //36 boots -> 39 helmet
            ItemStack currentArmor = currentInventory.getItem(i + 36);

            if (currentArmor == null || !currentArmor.equals(savedArmor[i]))
                currentInventory.setItem(i + 36, savedArmor[i]);
        }

        loadEnchantableItems(player);

        Common.broadcast("Loaded " + dungeonCache.getEnchantableItems().size() + " enchantable items!");

    }

    private void loadItemLeftInEnchanter(PlayerCache cache, SerializedMap map) throws IOException {
        String itemLeftString = map.get("Enchanting_Table_Contents",  String.class);

        if (itemLeftString == null)
            return;

        ItemStack itemLeftInEnchanter = BukkitSerialization.itemStackFromBase64(itemLeftString);

        if (itemLeftInEnchanter != null)
            cache.getTagger().setPlayerTag(EnchantingMenu.ITEM_STILL_IN_ENCHANTER,itemLeftInEnchanter);
    }

    private void loadEnchantableItems(Player player) {
        PlayerCache cache = PlayerCache.from(player);
        DungeonCache dungeonCache = DungeonCache.from(player);

        //load the items that are enchantable
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null)
                continue;
            if (TBSItemUtil.isEnchantable(item))
                dungeonCache.addEnchantableItem(item);
        }

        ItemStack itemLeftInEnchanter = cache.getTagger().getPlayerTag(EnchantingMenu.ITEM_STILL_IN_ENCHANTER);
        if (itemLeftInEnchanter != null)
            dungeonCache.addEnchantableItem(itemLeftInEnchanter);

    }


    public void disable() {
        if (pool != null)
            pool.close();

        if (jedis != null)
            jedis.close();
    }
}
