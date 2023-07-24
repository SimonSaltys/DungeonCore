package dev.tablesalt.dungeon.commands;

import dev.tablesalt.dungeon.tools.EnchantingWellTool;
import dev.tablesalt.gamelib.commands.GameSubCommand;

public final class SetupCommand extends GameSubCommand {
    private SetupCommand() {
        super("setup", 0, "", "gives you all the setup tools");
    }

    @Override
    protected void onCommand() {
        EnchantingWellTool.getInstance().give(getPlayer());
    }
}
