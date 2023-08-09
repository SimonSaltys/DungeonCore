package dev.tablesalt.dungeon.commands;

import dev.tablesalt.dungeon.database.DungeonCache;
import dev.tablesalt.gamelib.commands.GameSubCommand;

public final class GiveEnchantableItem extends GameSubCommand {
    private GiveEnchantableItem() {
        super("give", 0, "", "adds attribute to held item");
    }

    @Override
    protected void onCommand() {

        DungeonCache cache = DungeonCache.from(getPlayer());
        cache.giveMoney(100000);

    }
}
