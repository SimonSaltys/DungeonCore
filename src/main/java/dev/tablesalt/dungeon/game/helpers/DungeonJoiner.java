package dev.tablesalt.dungeon.game.helpers;

import dev.tablesalt.dungeon.game.DungeonGame;
import dev.tablesalt.gamelib.game.helpers.Game;
import dev.tablesalt.gamelib.game.helpers.PlayerJoiner;
import org.bukkit.entity.Player;

public class DungeonJoiner extends PlayerJoiner {

    private final DungeonGame game;
    public DungeonJoiner(DungeonGame game) {
        super(game);

        this.game = game;
    }

    @Override
    protected void onGameJoin(Player player) {

    }
}
