package dev.tablesalt.dungeon.game.helpers;

import dev.tablesalt.dungeon.game.DungeonGame;
import dev.tablesalt.dungeon.maps.DungeonMap;
import dev.tablesalt.dungeon.maps.spawnpoints.ExtractRegion;
import dev.tablesalt.dungeon.maps.spawnpoints.MonsterPoint;
import dev.tablesalt.gamelib.game.helpers.Stopper;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class DungeonStopper extends Stopper {

    private final DungeonGame game;

    public DungeonStopper(DungeonGame game) {
        super(game);

        this.game = game;
    }


    @Override
    protected void onGameStop() {
        resetSpawnPoints();
        resetExtractLocations();
        despawnLoot();
    }


    private void resetExtractLocations() {
        for (ExtractRegion region : game.getMapRotator().getCurrentMap().getExtractRegions()) {
            region.setActive(false);

        }

    }

    private void despawnLoot() {
        DungeonMap map = game.getMapRotator().getCurrentMap();
        map.getLootPoints().forEach(point -> {
            Block block = point.getLocation().clone().add(0,1,0).getBlock();

            if (block.getType().equals(Material.CHEST))
                block.setType(Material.AIR);
        });
    }

    private void resetSpawnPoints() {
        DungeonMap map = game.getMapRotator().getCurrentMap();

        if (map == null)
            return;

        for (MonsterPoint point : map.getMonsterPoints())
            point.setTriggered(false);

    }
}
