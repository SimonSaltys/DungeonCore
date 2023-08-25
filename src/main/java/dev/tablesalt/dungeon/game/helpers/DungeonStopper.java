package dev.tablesalt.dungeon.game.helpers;

import dev.tablesalt.dungeon.DungeonStaticSettings;
import dev.tablesalt.dungeon.game.DungeonGame;
import dev.tablesalt.dungeon.maps.spawnpoints.ExtractRegion;
import dev.tablesalt.dungeon.nms.PlayerCorpse;
import dev.tablesalt.dungeon.util.DungeonUtil;
import dev.tablesalt.gamelib.game.enums.GameJoinMode;
import dev.tablesalt.gamelib.game.helpers.Stopper;
import dev.tablesalt.gamelib.players.PlayerCache;
import org.mineacademy.fo.Common;

import java.util.List;

public class DungeonStopper extends Stopper {

    private final DungeonGame game;

    public DungeonStopper(DungeonGame game) {
        super(game);

        this.game = game;
    }


    @Override
    protected void onGameStop() {
        //todo leave and join players previously in the game.
        DungeonUtil.resetSpawnPoints(game);
        resetExtractLocations();
        DungeonUtil.despawnLoot(game);
        PlayerCorpse.removeAllCorpses();

        startNewGame();
    }

    private void startNewGame() {
        List<PlayerCache> playersToRejoin = game.getPlayerGetter().getPlayers(GameJoinMode.PLAYING);

        Common.runLater(DungeonStaticSettings.GameConfig.TIME_BETWEEN_GAMES.getTimeTicks(),() -> {
            for (PlayerCache cache : playersToRejoin)
                game.getPlayerJoiner().joinPlayer(cache.toPlayer(),GameJoinMode.PLAYING);
        });


    }


    private void resetExtractLocations() {
        for (ExtractRegion region : game.getMapRotator().getCurrentMap().getExtractRegions()) {
            region.setActive(false);
        }

    }
}
