package dev.tablesalt.dungeon.maps;
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
    private int maxMonstersSpawns, maxLootSpawns;

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

    }

    @Override
    protected void onSave() {
       set("player-spawn-points", playerSpawnPoints);
       set("monster-spawn-points", monsterSpawnPoints);
       set("max-monster-spawns", maxMonstersSpawns);
       set("max-loot-spawns", maxLootSpawns);
       set("loot-spawn-points", lootSpawnPoints);

       Common.broadcast("SAVING");

       super.onSave();
    }


    public boolean spawnPointsValid() {
       return Valid.isInRange(playerSpawnPoints.size(),game.getMinPlayers(),game.getMaxPlayers())
               && Valid.isInRange(monsterSpawnPoints.size(),1,maxMonstersSpawns);
    }
    @Override
    public boolean isSetup() {
        return super.isSetup() && spawnPointsValid();
    }

    /**
     * Toggles a monster spawn point at the given location.
     */
    public void toggleLocationInList(Player player, SpawnPoint spawnPoint, List<?> listToToggle) {

        boolean removed = iterateAndRemovePointFromList(spawnPoint, listToToggle);


        if (removed) {
            Common.tellNoPrefix(player, "&cRemoved &7point.");
            CompSound.BLOCK_STONE_BREAK.play(player);
            save();
            return;
        }

        boolean added = addSpawnPointToCorrectList(spawnPoint);

        if (added) {
            Common.tellNoPrefix(player, "&aAdded &7point.");
            CompSound.BLOCK_STONE_PLACE.play(player);
        } else {
            Common.tellNoPrefix(player, "&cCould not add point. &7Max points reached.");
            CompSound.BLOCK_ANVIL_LAND.play(player);
        }
       save();
    }

    public SpawnPoint hasPointWithLocation(Location location, List<?> listToFind) {
        for (Object obj : listToFind) {
            if (obj instanceof SpawnPoint checkPoint)
                if (checkPoint.getLocation().equals(location))
                    return checkPoint;
        }
        return null;
    }

    private boolean iterateAndRemovePointFromList(SpawnPoint spawnPoint, List<?> listToToggle) {
        Iterator<?> itr = listToToggle.iterator();

        while (itr.hasNext()) {
            Object point = itr.next();
            if (point instanceof SpawnPoint) {
                if (((SpawnPoint) point).getLocation().equals(spawnPoint.getLocation())) {
                    itr.remove();
                    return true;
                }
            }
        }

        return false;
    }

    private boolean addSpawnPointToCorrectList(SpawnPoint spawnPoint) {
        if(spawnPoint instanceof MonsterSpawnPoint) {
            if (maxMonstersSpawns > monsterSpawnPoints.size()) {
                monsterSpawnPoints.add((MonsterSpawnPoint) spawnPoint);
                return true;
            }
        } else if (spawnPoint instanceof LootSpawnPoint)
            if (maxLootSpawns > lootSpawnPoints.size()) {
                lootSpawnPoints.add((LootSpawnPoint) spawnPoint);
                return true;
            }
            return false;
    }
}
