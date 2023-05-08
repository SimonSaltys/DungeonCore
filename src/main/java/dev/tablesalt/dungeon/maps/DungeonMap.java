package dev.tablesalt.dungeon.maps;
import dev.tablesalt.dungeon.menu.MonsterSpawnMenu;
import dev.tablesalt.gameLib.lib.Common;
import dev.tablesalt.gameLib.lib.Valid;
import dev.tablesalt.gameLib.lib.remain.CompSound;
import dev.tablesalt.gamelib.game.helpers.Game;
import dev.tablesalt.gamelib.game.map.GameMap;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class DungeonMap extends GameMap {
    @Getter
    private LocationList playerSpawnPoints;
    @Getter
    private List<MonsterSpawnPoint> monsterSpawnPoints;
    @Getter
    private List<LootSpawnPoint> lootSpawnPoints;
    @Getter
    private int maxMonstersSpawns, maxLootSpawns, minLootSpawns;

    protected DungeonMap(String name, Game game) {
        super(name, game);
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        maxLootSpawns = getInteger("max-loot-spawns",10);
        maxMonstersSpawns = getInteger("max-monster-spawns",10);

        playerSpawnPoints = getLocationList("player-spawn-points");
        monsterSpawnPoints = getList("monster-spawn-points", MonsterSpawnPoint.class);
        lootSpawnPoints = getList("loot-spawn-points", LootSpawnPoint.class);
        minLootSpawns = getInteger("min-loot-spawns", 5);

    }

    @Override
    protected void onSave() {
       set("player-spawn-points", playerSpawnPoints);
       set("monster-spawn-points", monsterSpawnPoints);
       set("max-monster-spawns", maxMonstersSpawns);
       set("max-loot-spawns", maxLootSpawns);
       set("loot-spawn-points", lootSpawnPoints);
       set("min-loot-spawns", minLootSpawns);

       Common.broadcast("SAVING");

       super.onSave();
    }

    public boolean toggleLootSpawnPoint(Location location) {
        for (LootSpawnPoint point : lootSpawnPoints)
            if (point.getLocation().equals(location)) {
                lootSpawnPoints.remove(point);
                save();
                return false;
            }

        lootSpawnPoints.add(new LootSpawnPoint(location));
        save();
        return true;
    }

    public boolean toggleMonsterSpawnPoint(Location location) {
        for (MonsterSpawnPoint point : monsterSpawnPoints)
            if (point.getLocation().equals(location)) {
                monsterSpawnPoints.remove(point);
                save();
                return false;
            }
        monsterSpawnPoints.add(new MonsterSpawnPoint(location));
        save();
        return true;
    }

    public LootSpawnPoint getLootSpawnPoint(Location location) {
        for (LootSpawnPoint point : lootSpawnPoints)
            if (point.getLocation().equals(location))
                return point;
        return null;
    }

    public MonsterSpawnPoint getMonsterSpawnPoint(Location location) {
        for (MonsterSpawnPoint point : monsterSpawnPoints)
            if (point.getLocation().equals(location))
                return point;
        return null;
    }


    public boolean spawnPointsValid() {
       return Valid.isInRange(playerSpawnPoints.size(),game.getMinPlayers(),game.getMaxPlayers())
               && Valid.isInRange(monsterSpawnPoints.size(),1,maxMonstersSpawns) &&
               Valid.isInRange(lootSpawnPoints.size(),minLootSpawns,maxLootSpawns);
    }
    @Override
    public boolean isSetup() {
        return super.isSetup() && spawnPointsValid();
    }
}
