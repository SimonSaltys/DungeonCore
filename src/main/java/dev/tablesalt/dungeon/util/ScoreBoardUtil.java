package dev.tablesalt.dungeon.util;

import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.mineacademy.fo.model.SimpleScoreboard;

@UtilityClass
public class ScoreBoardUtil {



    public void displayScoreboard(Player player, SimpleScoreboard scoreboard) {
        SimpleScoreboard.clearBoardsFor(player);

        scoreboard.show(player);

    }
}
