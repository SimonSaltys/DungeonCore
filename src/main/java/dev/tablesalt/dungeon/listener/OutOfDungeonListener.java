package dev.tablesalt.dungeon.listener;

import dev.tablesalt.dungeon.database.Keys;
import dev.tablesalt.dungeon.game.scoreboard.HubScoreboard;
import dev.tablesalt.dungeon.menu.impl.EnchantingMenu;
import dev.tablesalt.dungeon.util.EntityUtil;
import dev.tablesalt.gamelib.event.PlayerLeaveGameEvent;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class OutOfDungeonListener implements Listener {


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        HubScoreboard.displayTo(event.getPlayer());
    }

    @EventHandler
    public void onPlayerLeaveGame(PlayerLeaveGameEvent event) {
        HubScoreboard.displayTo(event.getPlayer());
    }

    @EventHandler
    public void onBlockClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        Block block = event.getClickedBlock();

        if (block != null && isBlockEnchantingWell(block))
            EnchantingMenu.openEnchantMenu(player);
    }


    private boolean isBlockEnchantingWell(Block block) {
        TextDisplay display = EntityUtil.getClosestTextDisplay(block.getLocation().clone().add(0.5, 1, 0.5), 0.5);

        return display != null && display.getCustomName() != null && display.getCustomName().equals(Keys.DISPLAY_NAME);
    }


}
