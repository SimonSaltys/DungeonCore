package dev.tablesalt.dungeon.database;


import dev.tablesalt.dungeon.menu.enchanting.EnchantingMenu;
import dev.tablesalt.dungeon.util.MessageUtil;
import dev.tablesalt.dungeon.util.PlayerUtil;
import dev.tablesalt.gamelib.players.PlayerCache;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.collection.SerializedMap;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;


public class RedisDatabase  {

    @Getter
    private static final RedisDatabase instance = new RedisDatabase();

    private static final String PLAYER_ITEMS = "Player_Inventories:";

    private JedisPool pool;

    Jedis jedis;

    private RedisDatabase() {
    }

    public void connect() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(32);

        pool = new JedisPool(poolConfig,"localhost",6379,10000);
        jedis = pool.getResource();

       Common.log(isConnected() ? MessageUtil.makeSuccessful("Connected to redis") : MessageUtil.makeError("Could not connect to redis") );
    }

    public boolean isConnected() {
        return pool != null && jedis.isConnected();
    }

    public void saveItems(Player player) {
        if (!isConnected())
            return;

        PlayerInventory inventory = player.getInventory();
        PlayerCache cache = PlayerCache.from(player);

        ItemStack itemLeftInEnchanter = cache.getTagger().getPlayerTag(EnchantingMenu.ITEM_STILL_IN_ENCHANTER);

        SerializedMap inventoriesMap = SerializedMap.ofArray(
                "Main_Inventory", BukkitSerialization.itemStackArrayToBase64(PlayerUtil.getAllItemsExcludingArmor(player)),
                "Armor_Contents", BukkitSerialization.itemStackArrayToBase64(inventory.getArmorContents()),
                "Off_Hand_Contents", BukkitSerialization.itemStackToBase64(inventory.getItemInOffHand()),
                "Enchanting_Table_Contents", itemLeftInEnchanter != null ? BukkitSerialization.itemStackToBase64(itemLeftInEnchanter) : null);

        jedis.set(PLAYER_ITEMS + player.getUniqueId(), inventoriesMap.toJson());
    }

    public void loadItems(Player player) throws IOException {
        SerializedMap map = SerializedMap.fromJson(jedis.get(PLAYER_ITEMS + player.getUniqueId()));
        PlayerCache cache = PlayerCache.from(player);

        ItemStack itemLeftInEnchanter = cache.getTagger().getPlayerTag(EnchantingMenu.ITEM_STILL_IN_ENCHANTER);

        PlayerInventory currentInventory = player.getInventory();
        ItemStack[] savedInventory = BukkitSerialization.itemStackArrayFromBase64(map.get("Main_Inventory",String.class));
        ItemStack[] savedArmor = BukkitSerialization.itemStackArrayFromBase64(map.get("Armor_Contents", String.class));
        ItemStack savedOffHand = BukkitSerialization.itemStackFromBase64(map.get("Off_Hand_Contents", String.class));

        //load enchanting menu if any item was left there
        if (itemLeftInEnchanter != null)
            cache.getTagger().setPlayerTag(EnchantingMenu.ITEM_STILL_IN_ENCHANTER,itemLeftInEnchanter);

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
    }

    public void disable() {
       if (pool != null)
           pool.close();

       if (jedis != null)
          jedis.close();
    }
}
