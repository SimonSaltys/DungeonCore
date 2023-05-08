package dev.tablesalt.dungeon.maps;

import dev.tablesalt.gameLib.lib.model.ConfigSerializable;
import org.bukkit.Location;

public abstract class SpawnPoint implements ConfigSerializable {

    protected abstract void spawn();

   protected abstract  Location getLocation();

}
