package dev.tablesalt.dungeon.game.helpers;

import dev.tablesalt.dungeon.game.DungeonGame;
import dev.tablesalt.dungeon.maps.spawnpoints.ExtractRegion;
import dev.tablesalt.dungeon.nms.PlayerCorpse;
import dev.tablesalt.dungeon.util.DungeonUtil;
import dev.tablesalt.gamelib.game.helpers.Stopper;

public class DungeonStopper extends Stopper {

    private final DungeonGame game;

    public DungeonStopper(DungeonGame game) {
        super(game);

        this.game = game;
    }


    @Override
    protected void onGameStop() {
        DungeonUtil.resetSpawnPoints(game);
        resetExtractLocations();
        DungeonUtil.despawnLoot(game);
        PlayerCorpse.removeAllCorpses();
    }


    private void resetExtractLocations() {
        for (ExtractRegion region : game.getMapRotator().getCurrentMap().getExtractRegions()) {
            region.setActive(false);

        }

    }
}
