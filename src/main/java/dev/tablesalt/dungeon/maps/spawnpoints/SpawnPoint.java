package dev.tablesalt.dungeon.maps.spawnpoints;

import org.bukkit.Location;
import org.mineacademy.fo.model.ConfigSerializable;

public abstract class SpawnPoint implements ConfigSerializable {
    protected abstract void spawn();

   protected abstract  Location getLocation();

}
