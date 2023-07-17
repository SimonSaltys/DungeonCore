package dev.tablesalt.dungeon.database;

import dev.tablesalt.dungeon.item.ItemAttribute;
import dev.tablesalt.gamelib.players.PlayerCache;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class DungeonCache {

    private static final Map<UUID, DungeonCache> cacheMap = new HashMap<>();

    private final UUID uniqueId;

    private final String playerName;

    private DungeonCache(String name, UUID uniqueId) {
        this.uniqueId = uniqueId;
        this.playerName = name;
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
