package dev.tablesalt.dungeon.item.global.impl;

import dev.tablesalt.dungeon.item.global.GlobalItem;
import dev.tablesalt.dungeon.item.global.GlobalType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Can view all active servers, their player count, status, and time left.
 * Then connect the player to the server if these variables permit.
 */
public class ServerJoinItem implements GlobalItem {
    @Override
    public void onClick(Player player, GlobalItem item, PlayerInteractEvent event) {
    }

    @Override
    public void getItem() {

    }

    @Override
    public GlobalType getGlobalType() {
        return null;
    }
}
