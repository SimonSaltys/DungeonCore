package dev.tablesalt.dungeon.listener;

import dev.tablesalt.dungeon.database.DungeonCache;
import dev.tablesalt.dungeon.database.MariaDatabase;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.PlayerUtil;

public class DatabaseListener implements Listener {
    private static final MariaDatabase db = MariaDatabase.getInstance();

    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        db.loadCache(player,(cache) -> {});
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        db.saveCache(player);
    }
}
