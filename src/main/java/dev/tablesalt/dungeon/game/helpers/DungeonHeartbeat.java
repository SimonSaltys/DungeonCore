package dev.tablesalt.dungeon.game.helpers;

import dev.tablesalt.dungeon.DungeonStaticSettings;
import dev.tablesalt.dungeon.game.DungeonGame;
import dev.tablesalt.dungeon.maps.DungeonMap;
import dev.tablesalt.dungeon.maps.spawnpoints.ExtractRegion;
import dev.tablesalt.dungeon.maps.spawnpoints.MonsterPoint;
import dev.tablesalt.dungeon.util.DungeonUtil;
import dev.tablesalt.gamelib.game.helpers.GameHeartbeat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.mineacademy.fo.remain.Remain;
import org.mineacademy.fo.visual.VisualizedRegion;

public class DungeonHeartbeat extends GameHeartbeat {

    private final DungeonGame game;

    private static final int TEN_MINUTES = 600;

    private static final int FIVE_MINUTES = 300;

    private int secondsPassed = 0;

    public DungeonHeartbeat(DungeonGame game) {
        super(game, DungeonStaticSettings.GameConfig.timeUntilStop);
        this.game = game;
    }

    @Override
    protected void onTick() {

        if (secondsPassed >= FIVE_MINUTES) {
            DungeonUtil.despawnLoot(game);
            DungeonUtil.spawnLoot(game);
            secondsPassed = 0;
        }

        if (getTimeLeft() == TEN_MINUTES) {
            game.getGameBroadcaster().broadcastInfo("Instance is closing in &610 minutes!");
        }

        if (getTimeLeft() == FIVE_MINUTES) {
            game.getGameBroadcaster().broadcastInfo("Instance is closing in &65 minutes!");
        }

        secondsPassed++;
    }

    @Override
    protected void onTickFast() {
        tickMobSpawnPoints();
        tickExtractLocations();
    }

    @Override
    protected void onEnd() {
        super.onEnd();
        game.getStopper().stop();
    }


    private void tickExtractLocations() {
        DungeonMap map = game.getMapRotator().getCurrentMap();

        if (map == null)
            return;

        for (ExtractRegion extractRegion : map.getExtractRegions()) {
            VisualizedRegion region = extractRegion.getRegion();

            if (!extractRegion.isActive())
                return;

            for (Entity entity : Remain.getNearbyEntities(region.getCenter(), 20))
                if (entity instanceof Player player) {
                    if (region.isWithin(player.getLocation()))
                        extractRegion.startExtractionFor(player);

                    if (!region.canSeeParticles(player))
                        region.showParticles(player);
                }
        }
    }

    private void tickMobSpawnPoints() {
        DungeonMap map = game.getMapRotator().getCurrentMap();

        if (map == null)
            return;

        for (MonsterPoint point : map.getMonsterPoints())
            for (Entity entity : Remain.getNearbyEntities(point.getLocation(), point.getTriggerRadius()))
                if (entity instanceof Player)
                    point.spawn();
    }


}
