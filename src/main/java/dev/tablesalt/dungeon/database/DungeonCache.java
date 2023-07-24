package dev.tablesalt.dungeon.database;

import dev.tablesalt.dungeon.util.TBSItemUtil;
import lombok.Getter;
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

    private int moneyAmount;

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


    public void giveMoney(int amount) {

    }

    public void takeMoney(int amount) {

    }

    public void setMoney(int amount) {
        this.moneyAmount = amount;
    }

    public int getMoney() {
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
