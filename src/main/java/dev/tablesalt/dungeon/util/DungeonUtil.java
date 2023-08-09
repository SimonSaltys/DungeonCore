package dev.tablesalt.dungeon.util;

import dev.tablesalt.dungeon.game.DungeonGame;
import dev.tablesalt.dungeon.maps.DungeonMap;
import dev.tablesalt.dungeon.maps.spawnpoints.LootPoint;
import dev.tablesalt.dungeon.maps.spawnpoints.MonsterPoint;
import dev.tablesalt.gamelib.game.utils.GameUtil;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.mineacademy.fo.RandomUtil;
import org.mineacademy.fo.model.RandomNoRepeatPicker;

@UtilityClass
public class DungeonUtil {


    public void spawnLoot(DungeonGame game) {
        DungeonMap map = game.getMapRotator().getCurrentMap();
        RandomNoRepeatPicker<LootPoint> lootSpawnPoints = RandomNoRepeatPicker.newPicker(LootPoint.class);
        lootSpawnPoints.setItems(map.getLootPoints());

        int lootCount = RandomUtil.nextBetween(map.getMinLootSpawns(), map.getMaxLootSpawns());

        for (int i = 0; i < lootCount; i++) {
            LootPoint lootPoint = lootSpawnPoints.pickRandom();

            if (lootPoint != null)
                lootPoint.spawn();
        }
    }

    public void teleportToLobby(Player player, DungeonGame game) {
        Location location = RandomUtil.nextItem(game.getMapRotator().getCurrentMap().getPlayerSpawnPoints().getLocations());
        GameUtil.teleport(player, location);
    }


    public void despawnLoot(DungeonGame game) {
        DungeonMap map = game.getMapRotator().getCurrentMap();
        map.getLootPoints().forEach(point -> {
            Block block = point.getLocation().clone().add(0, 1, 0).getBlock();

            if (block.getType().equals(Material.CHEST))
                block.setType(Material.AIR);
        });
    }

    public void resetSpawnPoints(DungeonGame game) {
        DungeonMap map = game.getMapRotator().getCurrentMap();

        if (map == null)
            return;

        for (MonsterPoint point : map.getMonsterPoints())
            point.setTriggered(false);

    }


}
