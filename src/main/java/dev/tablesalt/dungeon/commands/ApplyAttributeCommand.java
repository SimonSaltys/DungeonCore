package dev.tablesalt.dungeon.commands;

import dev.tablesalt.dungeon.item.impl.TestAttribute;
import dev.tablesalt.dungeon.menu.EnchantingMenu;
import dev.tablesalt.gamelib.commands.GameSubCommand;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;

import java.util.List;

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
