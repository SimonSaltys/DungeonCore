package dev.tablesalt.dungeon.maps;

import dev.tablesalt.dungeon.util.EntityUtil;
import dev.tablesalt.gameLib.lib.collection.SerializedMap;
import dev.tablesalt.gameLib.lib.model.ConfigSerializable;
import dev.tablesalt.gameLib.lib.settings.FileConfig;
import dev.tablesalt.gamelib.game.map.GameMap;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.*;

import java.util.List;

public class MonsterSpawnPoint extends SpawnPoint {

    private final Location location;
    @Getter @Setter
    private EntityType entity;
    @Getter @Setter
    private double triggerRadius;
    @Getter @Setter
    private int amountToSpawn;

    @Setter
    private boolean triggered = false;

    public MonsterSpawnPoint(Location location) {
        this.location = location;
        this.entity = EntityType.ZOMBIE;
    }

    @Override
    public void spawn() {
        if (triggered) return;

        for (int i = 0; i < amountToSpawn; i++)
            EntityUtil.spawnEntity(location,entity,triggerRadius / 2);

       triggered = true;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public SerializedMap serialize() {
        return SerializedMap.ofArray(
                "location", location,
                "entity", entity,
                "trigger-radius", triggerRadius,
                "amount-to-spawn", amountToSpawn
        );
    }

    public static MonsterSpawnPoint deserialize(SerializedMap map) {
       MonsterSpawnPoint spawnPoint = new MonsterSpawnPoint(map.getLocation("location"));
       spawnPoint.setEntity(map.get("entity", EntityType.class));
       spawnPoint.setTriggerRadius(map.getDouble("trigger-radius",3.5));
       spawnPoint.setAmountToSpawn(map.getInteger("amount-to-spawn",1));

       return spawnPoint;
    }
}
