package dev.tablesalt.dungeon.game.helpers;

import dev.tablesalt.dungeon.game.DungeonGame;
import dev.tablesalt.dungeon.util.DungeonUtil;
import dev.tablesalt.gamelib.game.helpers.PlayerJoiner;
import org.bukkit.entity.Player;

public class DungeonJoiner extends PlayerJoiner {

    private final DungeonGame game;

    public DungeonJoiner(DungeonGame game) {
        super(game);

        this.game = game;
    }

    @Override
    protected boolean cleanPlayerOnJoin() {
        return false;
    }

    @Override
    protected boolean canJoinExtendedLogic() {
        return true;
    }

    @Override
    protected void onGameJoin(Player player) {
            //
    }
}
