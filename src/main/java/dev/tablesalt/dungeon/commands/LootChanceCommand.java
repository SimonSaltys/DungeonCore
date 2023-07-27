package dev.tablesalt.dungeon.commands;

import dev.tablesalt.dungeon.menu.impl.LootChanceMenu;
import dev.tablesalt.gamelib.commands.GameSubCommand;

public final class LootChanceCommand extends GameSubCommand {

    private LootChanceCommand() {
        super("loot", 0, "", "edits the loot configurations");
    }

    @Override
    protected void onCommand() {
        LootChanceMenu.openMenu(getPlayer());
    }
}
