package dev.tablesalt.dungeon.game.helpers;

import dev.tablesalt.dungeon.game.DungeonGame;
import dev.tablesalt.dungeon.maps.DungeonMap;
import dev.tablesalt.dungeon.maps.MonsterSpawnPoint;
import dev.tablesalt.gameLib.lib.remain.Remain;
import dev.tablesalt.gamelib.game.helpers.GameHeartbeat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class DungeonHeartbeat extends GameHeartbeat {

    private final DungeonGame game;
    public DungeonHeartbeat(DungeonGame game) {
        super(game);

        this.game = game;
    }

    @Override
    protected void onTick() {
        tickMobSpawnPoints();
        super.onTick();
    }
    @Override
    protected void onEnd() {
        super.onEnd();

        resetSpawnPoints();
        game.getStopper().stop();
    }

    private void tickMobSpawnPoints() {
        DungeonMap map = game.getMapRotator().getCurrentMap();

        if (map == null)
            return;

        for (MonsterSpawnPoint point : map.getMonsterSpawnPoints())
            for(Entity entity : Remain.getNearbyEntities(point.getLocation(),point.getTriggerRadius()))
                if (entity instanceof Player)
                    point.spawn();
    }

    private void resetSpawnPoints() {
        DungeonMap map = game.getMapRotator().getCurrentMap();

        if (map == null)
            return;

        for (MonsterSpawnPoint point : map.getMonsterSpawnPoints())
            point.setTriggered(false);

    }
}
