package dev.tablesalt.dungeon.database;

import dev.tablesalt.dungeon.util.TBSItemUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.mineacademy.fo.remain.Remain;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Holds information that is useful when
 * a player is on the server. This information
 * is highly volatile, and it is recommended to save
 * often to a database if you want to persist data.
 */
public class DungeonCache {

    private static final Map<UUID, DungeonCache> cacheMap = new HashMap<>();
    @Getter
    private final UUID uniqueId;
    @Getter
    private final String playerName;

    private final List<EnchantableItem> enchantableItems = new ArrayList<>();

    @Getter
    @Setter
    private EnchantableItem itemInEnchanter;

    private double moneyAmount;

    private DungeonCache(String name, UUID uniqueId) {
        this.uniqueId = uniqueId;
        this.playerName = name;

    }


    /*----------------------------------------------------------------*/
    /* ENCHANTABLE ITEM METHODS */
    /*----------------------------------------------------------------*/

    /**
     * Removes enchantable items that are no longer
     * in the players actual inventory as itemstacks.
     */
    public void removeOldEnchantableItems(Player player) {
        PlayerInventory inventory = player.getInventory();

        enchantableItems.removeIf(item -> !inventory.contains(item.compileToItemStack()));
    }

    public void addEnchantableItem(EnchantableItem item) {
        if (item != null)
            if (!enchantableItems.contains(item)) {
                enchantableItems.add(item);
            }
    }

    /**
     * Attempts to parse and add the itemstack
     * to the players cache if not already contained.
     */
    public void addEnchantableItem(ItemStack item) {
        EnchantableItem enchantableItem = EnchantableItem.fromItemStack(item);

        if (enchantableItem != null)
            if (!enchantableItems.contains(enchantableItem)) {
                enchantableItems.add(enchantableItem);
            }
    }

    public void updateEnchantableItem(EnchantableItem item) {
        enchantableItems.remove(item);
        enchantableItems.add(item);
    }

    /**
     * Attempts to parse and remove the itemstack
     * in the players inventory.
     */
    public void removeEnchantableItem(ItemStack itemStack) {
        EnchantableItem enchantableItem = EnchantableItem.fromItemStack(itemStack);
        if (enchantableItem == null)
            return;

        enchantableItems.remove(enchantableItem);
    }

    /**
     * Attempts to get the enchantable item
     * from the provided item_stack stored in
     * this players cache.
     */
    public EnchantableItem getEnchantableItem(ItemStack item) {
        UUID itemUUID = TBSItemUtil.getItemsUUID(item);


        if (itemUUID != null)
            for (EnchantableItem enchantableItem : enchantableItems)
                if (enchantableItem.getUuid().equals(itemUUID))
                    return enchantableItem;

        return null;
    }


    public List<EnchantableItem> getEnchantableItems() {
        return Collections.unmodifiableList(enchantableItems);
    }



    /*----------------------------------------------------------------*/
    /* MONEY METHODS */
    /*----------------------------------------------------------------*/


    public void giveMoney(double amount) {
        moneyAmount += amount;
    }

    public void takeMoney(int amount) {
        moneyAmount = moneyAmount - amount;

        if (moneyAmount < 0)
            moneyAmount = 0;
    }

    public void setMoney(double amount) {
        this.moneyAmount = amount;
    }

    public double getMoney() {
        return moneyAmount;
    }


    @Nullable
    public Player toPlayer() {
        Player player = Remain.getPlayerByUUID(this.uniqueId);
        return player != null && player.isOnline() ? player : null;
    }

    /**
     * Return or create new player cache for the given player
     *
     * @param player
     * @return
     */
    public static DungeonCache from(Player player) {
        synchronized (cacheMap) {
            final UUID uniqueId = player.getUniqueId();
            final String playerName = player.getName();

            DungeonCache cache = cacheMap.get(uniqueId);

            if (cache == null) {
                cache = new DungeonCache(playerName, uniqueId);

                cacheMap.put(uniqueId, cache);
            }

            return cache;
        }
    }

    public static void purge() {
        cacheMap.clear();
    }


}
