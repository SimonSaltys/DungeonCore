package dev.tablesalt.dungeon.listener;

import dev.tablesalt.dungeon.menu.enchanting.EnchantingMenu;
import dev.tablesalt.dungeon.tools.EnchantingWellTool;
import dev.tablesalt.dungeon.util.EntityUtil;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.mineacademy.fo.Common;

public class OutOfDungeonListener implements Listener {


    @EventHandler
    public void onBlockClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        Block block = event.getClickedBlock();

        if (block != null && isBlockEnchantingWell(block))
            EnchantingMenu.openEnchantMenu(player);
    }

    private boolean isBlockEnchantingWell(Block block) {
       TextDisplay display = EntityUtil.getClosestTextDisplay(block.getLocation().clone().add(0.5,1,0.5),0.5);


        return display != null && display.getCustomName() != null && display.getCustomName().equals(EnchantingWellTool.DISPLAY_NAME);
    }


}
