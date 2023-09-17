package dev.tablesalt.dungeon.item.global;

import net.minecraft.network.PacketListener;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Items that should be on the player at all times
 * either in game, not in game, or both.
 */
public interface GlobalItem {

    void onClick(Player player, GlobalItem item, PlayerInteractEvent event);

    void getItem();


    GlobalType getGlobalType();




}
