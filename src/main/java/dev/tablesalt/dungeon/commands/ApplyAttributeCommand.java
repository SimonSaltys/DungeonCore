package dev.tablesalt.dungeon.commands;

import dev.tablesalt.dungeon.database.RedisDatabase;
import dev.tablesalt.dungeon.menu.enchanting.EnchantingMenu;
import dev.tablesalt.gamelib.commands.GameSubCommand;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public final class ApplyAttributeCommand extends GameSubCommand {
    private ApplyAttributeCommand() {
        super("apply", 0, "", "adds attribute to held item");
    }

    @Override
    protected void onCommand() {
        EnchantingMenu.openEnchantMenu(getPlayer());

    }
}
