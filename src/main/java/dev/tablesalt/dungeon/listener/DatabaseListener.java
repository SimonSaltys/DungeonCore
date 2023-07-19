package dev.tablesalt.dungeon.listener;

import dev.tablesalt.dungeon.database.DungeonCache;
import dev.tablesalt.dungeon.database.EnchantableItem;
import dev.tablesalt.dungeon.database.RedisDatabase;
import dev.tablesalt.dungeon.util.MessageUtil;
import jdk.jfr.Enabled;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;

import java.io.IOException;

public class DatabaseListener implements Listener {


    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();

        try {
            RedisDatabase.getInstance().loadItems(player);
        } catch (IOException e) {
            Common.log(MessageUtil.makeError("Could not load items from redis cache for: " + player.getName() +
                    ". Is Redis connected? This is a serious issue, would recommend shutting down server to address"));

            e.printStackTrace();
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        RedisDatabase.getInstance().saveItems(player);
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
