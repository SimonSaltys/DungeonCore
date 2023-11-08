package dev.tablesalt.dungeon.commands;

import dev.tablesalt.dungeon.database.DungeonCache;
import dev.tablesalt.dungeon.database.EnchantableItem;
import dev.tablesalt.dungeon.database.MariaDatabase;
import dev.tablesalt.dungeon.item.ItemAttribute;
import dev.tablesalt.dungeon.tools.EnchantingWellTool;
import dev.tablesalt.gamelib.commands.GameSubCommand;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;

import java.util.Arrays;
import java.util.List;

public final class SetupCommand extends GameSubCommand {
    private SetupCommand() {
        super("database", 1, "load|save", "loads and saves from database");
    }

    @Override
    protected void onCommand() {

        Player player = getPlayer();
        String arg = args[0];

        if (arg.equalsIgnoreCase("load"))
            MariaDatabase.getInstance().loadCache(player,cache -> {});

        else if(arg.equalsIgnoreCase("save"))
            MariaDatabase.getInstance().saveCache(player);


        else if(arg.equalsIgnoreCase("remove")) {
            MariaDatabase.getInstance().cleanCache(player);
        }

        else
            tellError("Please use the arguments save or load.");
    }


    @Override
    protected List<String> tabComplete() {
        if (args.length == 1)
            return Arrays.asList("save","load","remove");

        return NO_COMPLETE;
    }
}
