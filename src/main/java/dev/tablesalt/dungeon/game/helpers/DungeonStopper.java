package dev.tablesalt.dungeon.game.helpers;

import dev.tablesalt.dungeon.game.DungeonGame;
import dev.tablesalt.dungeon.maps.DungeonMap;
import dev.tablesalt.gameLib.lib.Common;
import dev.tablesalt.gamelib.game.helpers.Game;
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
        despawnLoot();
    }


    private void despawnLoot() {
        DungeonMap map = game.getMapRotator().getCurrentMap();
        map.getLootSpawnPoints().forEach(point -> {
            Block block = point.getLocation().clone().add(0,1,0).getBlock();

            if (block.getType().equals(Material.CHEST))
                block.setType(Material.AIR);
        });
    }
}
