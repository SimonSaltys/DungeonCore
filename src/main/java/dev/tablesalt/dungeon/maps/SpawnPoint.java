package dev.tablesalt.dungeon.maps;

import dev.tablesalt.gameLib.lib.model.ConfigSerializable;
import org.bukkit.Location;

public abstract class SpawnPoint implements ConfigSerializable {

    abstract void spawn();

   abstract  Location getLocation();

}
