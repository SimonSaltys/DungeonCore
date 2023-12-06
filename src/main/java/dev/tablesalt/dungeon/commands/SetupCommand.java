package dev.tablesalt.dungeon.commands;
import dev.tablesalt.dungeon.database.MariaDatabase;
import dev.tablesalt.dungeon.tools.EnchantingWellTool;
import dev.tablesalt.gamelib.commands.GameSubCommand;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public final class SetupCommand extends GameSubCommand {
    private SetupCommand() {
        super("setup", 0, "", "whatever i deem to be");
    }

    @Override
    protected void onCommand() {

       Player player = getPlayer();

        EnchantingWellTool.getInstance().give(player);
    }

}
