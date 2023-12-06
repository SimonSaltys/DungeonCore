package dev.tablesalt.dungeon.util;

import dev.tablesalt.dungeon.game.scoreboard.HubScoreboard;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.mineacademy.fo.model.SimpleScoreboard;
import org.mineacademy.fo.remain.Remain;

@UtilityClass
public class ScoreBoardUtil {
    public void displayScoreboard(Player player, SimpleScoreboard scoreboard) {
        SimpleScoreboard.clearBoardsFor(player);

        scoreboard.show(player);

    }

    public void displayHubScoreboardToAll() {
        for (Player player : Remain.getOnlinePlayers()) {
            SimpleScoreboard.clearBoardsFor(player);

            HubScoreboard.displayTo(player);
        }
    }
}
