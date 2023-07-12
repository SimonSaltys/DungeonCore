package dev.tablesalt.dungeon.commands;

import dev.tablesalt.dungeon.menu.enchanting.EnchantingMenu;
import dev.tablesalt.gamelib.commands.GameSubCommand;
import org.bukkit.entity.Player;

public final class ApplyAttributeCommand extends GameSubCommand {
    private ApplyAttributeCommand() {
        super("menu", 0, "", "adds attribute to held item");
    }

    @Override
    protected void onCommand() {
        Player player = getPlayer();

        EnchantingMenu.openEnchantMenu(player);

    }


}
