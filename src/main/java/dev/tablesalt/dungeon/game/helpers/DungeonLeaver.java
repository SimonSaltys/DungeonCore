package dev.tablesalt.dungeon.game.helpers;

import dev.tablesalt.dungeon.game.DungeonGame;
import dev.tablesalt.gameLib.lib.model.Replacer;
import dev.tablesalt.gamelib.game.enums.LeaveReason;
import dev.tablesalt.gamelib.game.helpers.PlayerLeaver;
import dev.tablesalt.gamelib.game.utils.Message;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

public class DungeonLeaver extends PlayerLeaver {
    private final DungeonGame game;
    public DungeonLeaver(DungeonGame game) {
        super(game);

        this.game = game;
    }

    public void leavePlayerBecauseExtracted(Player player) {

    }

    public void leavePlayerBecauseDied(Player player) {

    }


}
