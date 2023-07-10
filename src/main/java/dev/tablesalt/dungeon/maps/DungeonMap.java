package dev.tablesalt.dungeon.maps;
import dev.tablesalt.dungeon.maps.spawnpoints.ExtractRegion;
import dev.tablesalt.dungeon.maps.spawnpoints.LootPoint;
import dev.tablesalt.dungeon.maps.spawnpoints.MonsterPoint;

import dev.tablesalt.gamelib.game.helpers.Game;
import dev.tablesalt.gamelib.game.map.GameMap;
import lombok.Getter;
import org.bukkit.Location;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.visual.VisualizedRegion;

import java.util.*;

public class DungeonMap extends GameMap {
    @Getter
    private LocationList playerSpawnPoints;
    @Getter
    private List<MonsterPoint> monsterPoints;
    @Getter
    private List<LootPoint> lootPoints;

    @Getter
    private List<ExtractRegion> extractRegions;
    @Getter
    private int extractRegionsToActivate;

    @Getter
    private int maxMonstersSpawns, maxLootSpawns, minLootSpawns, extractRegionAmount;

    protected DungeonMap(String name, Game game) {
        super(name, game);
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        maxLootSpawns = getInteger("max-loot-spawns",10);
        maxMonstersSpawns = getInteger("max-monster-spawns",10);
        extractRegionAmount = getInteger("max-extract-regions",4);
        extractRegionsToActivate = getInteger("extract-regions-to-activate",1);

        playerSpawnPoints = getLocationList("player-spawn-points");
        monsterPoints = getList("monster-spawn-points", MonsterPoint.class);
        lootPoints = getList("loot-spawn-points", LootPoint.class);
        minLootSpawns = getInteger("min-loot-spawns", 5);
        extractRegions = getList("extract-regions", ExtractRegion.class);

    }

    @Override
    protected void onSave() {
       set("player-spawn-points", playerSpawnPoints);
       set("monster-spawn-points", monsterPoints);
       set("max-monster-spawns", maxMonstersSpawns);
       set("max-loot-spawns", maxLootSpawns);
       set("loot-spawn-points", lootPoints);
       set("min-loot-spawns", minLootSpawns);
       set("extract-regions",extractRegions);
       set("max-extract-regions", extractRegionAmount);
       set("extract-regions-to-activate", extractRegionsToActivate);

       super.onSave();
    }

    public ExtractRegion findExtractRegion(Location location) {
        for (ExtractRegion region : extractRegions)
            if (region.getRegion().isWithin(location))
                return region;
        return null;
    }

    public List<VisualizedRegion> getVisualizedExtractRegions() {
        return Common.convert(extractRegions, ExtractRegion::getRegion);
    }



    public void addExtractRegion(VisualizedRegion region) {
            extractRegions.add(new ExtractRegion(region));

        save();
    }

    public boolean toggleLootSpawnPoint(Location location) {
        for (LootPoint point : lootPoints)
            if (point.getLocation().equals(location)) {
                lootPoints.remove(point);
                save();
                return false;
            }

        lootPoints.add(new LootPoint(location));
        save();
        return true;
    }

    public boolean toggleMonsterSpawnPoint(Location location) {
        for (MonsterPoint point : monsterPoints)
            if (point.getLocation().equals(location)) {
                monsterPoints.remove(point);
                save();
                return false;
            }
        monsterPoints.add(new MonsterPoint(location));
        save();
        return true;
    }

    public LootPoint getLootSpawnPoint(Location location) {
        for (LootPoint point : lootPoints)
            if (point.getLocation().equals(location))
                return point;
        return null;
    }

    public MonsterPoint getMonsterSpawnPoint(Location location) {
        for (MonsterPoint point : monsterPoints)
            if (point.getLocation().equals(location))
                return point;
        return null;
    }


    public boolean spawnPointsValid() {
       return Valid.isInRange(playerSpawnPoints.size(),game.getMinPlayers(),game.getMaxPlayers())
               && Valid.isInRange(monsterPoints.size(),1,maxMonstersSpawns) &&
               Valid.isInRange(lootPoints.size(),minLootSpawns,maxLootSpawns);
    }

    public boolean extractRegionsValid() {
        for (ExtractRegion region : extractRegions)
            if (region.getRegion() == null || !region.getRegion().isWhole())
                return false;
        return true;
    }
    @Override
    public boolean isSetup() {
        return super.isSetup() && spawnPointsValid() && extractRegionsValid();
    }
}
