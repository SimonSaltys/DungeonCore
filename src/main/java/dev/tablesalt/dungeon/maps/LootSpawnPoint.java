package dev.tablesalt.dungeon.maps;

import dev.tablesalt.gameLib.lib.collection.SerializedMap;
import dev.tablesalt.gameLib.lib.remain.CompMaterial;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

public class LootSpawnPoint extends SpawnPoint {

    private final Location location;
    @Getter @Setter
    private CompMaterial loot;

    public LootSpawnPoint(Location location) {
        this.location = location;
    }


    @Override
    void spawn() {

    }

    @Override
    Location getLocation() {
        return location;
    }

    @Override
    public SerializedMap serialize() {
        return SerializedMap.ofArray(
                    "location", location,
                    "loot", loot
            );
    }

    public static LootSpawnPoint deserialize(SerializedMap map) {
        LootSpawnPoint spawnPoint = new LootSpawnPoint(map.getLocation("location"));
        spawnPoint.setLoot(map.get("loot", CompMaterial.class));

        return spawnPoint;
    }
}
