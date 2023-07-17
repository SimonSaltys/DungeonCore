package dev.tablesalt.dungeon.database;

import dev.tablesalt.dungeon.item.ItemAttribute;
import dev.tablesalt.dungeon.util.TBSItemUtil;
import dev.tablesalt.gamelib.players.PlayerCache;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.ItemUtil;

import java.util.*;

public class DungeonCache {

    private static final Map<UUID, DungeonCache> cacheMap = new HashMap<>();
    @Getter
    private final UUID uniqueId;
    @Getter

    private final String playerName;

    private final List<EnchantableItem> enchantableItems = new ArrayList<>();

    private DungeonCache(String name, UUID uniqueId) {
        this.uniqueId = uniqueId;
        this.playerName = name;
    }

    public void addEnchantableItem(EnchantableItem item) {
        if (!enchantableItems.contains(item))
            enchantableItems.add(item);
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
}
