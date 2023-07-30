package dev.tablesalt.dungeon.listener;

import dev.tablesalt.dungeon.database.DungeonCache;
import dev.tablesalt.dungeon.database.RedisDatabase;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class DatabaseListener implements Listener {


    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();

        RedisDatabase.getInstance().load(player);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        RedisDatabase.getInstance().save(player);
    }

    @EventHandler
    public void onItemDropped(PlayerDropItemEvent event) {
        DungeonCache cache = DungeonCache.from(event.getPlayer());
        cache.removeEnchantableItem(event.getItemDrop().getItemStack());
    }
    @EventHandler
    public void onItemPickedUp(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player))
            return;

        DungeonCache cache = DungeonCache.from(player);
        cache.addEnchantableItem(event.getItem().getItemStack());
    }
}
