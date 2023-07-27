package dev.tablesalt.dungeon.game.scoreboard;

import dev.tablesalt.dungeon.database.DungeonCache;
import dev.tablesalt.dungeon.util.ScoreBoardUtil;
import dev.tablesalt.gamelib.players.PlayerCache;
import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.mineacademy.fo.model.Replacer;
import org.mineacademy.fo.model.SimpleScoreboard;

public class HubScoreboard extends SimpleScoreboard {

    private final Player player;

    private HubScoreboard(Player player) {
        this.player = player;
        this.setTitle("&8----- &f" + "Welcome!" + "&8-----");
        this.setTheme(ChatColor.WHITE, ChatColor.GRAY);
        this.setUpdateDelayTicks(10);

        addRows("Rank: {rank}",
                        "Gold: {money}",
                        "ect...");
    }

    @Override
    protected String replaceVariables(@NonNull Player player, @NonNull String message) {
        DungeonCache cache = DungeonCache.from(player);

      return Replacer.replaceArray(message,"rank", "todo", "money","&6" + cache.getMoney() + "g");
    }

    @Override
    protected void onUpdate() {
        if (PlayerCache.from(player).getGameIdentifier().hasGame())
            hide(player);
    }

    public static void displayTo(Player player) {
        ScoreBoardUtil.displayScoreboard(player, new HubScoreboard(player));
    }

}
