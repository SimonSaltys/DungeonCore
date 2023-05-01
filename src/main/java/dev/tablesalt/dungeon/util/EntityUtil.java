package dev.tablesalt.dungeon.util;

import com.earth2me.essentials.utils.LocationUtil;
import dev.tablesalt.gameLib.lib.RandomUtil;
import dev.tablesalt.gamelib.game.utils.GameUtil;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

@UtilityClass
public class EntityUtil {

    public void spawnEntity(Location location, EntityType entity, double maxDistanceFromCenter) {

        Location spawnLocation = RandomUtil.nextLocation(location,0.1 ,maxDistanceFromCenter + 0.1,false);

        World worldToSpawn = spawnLocation.getWorld();
       if (worldToSpawn != null)
           worldToSpawn.spawnEntity(spawnLocation.add(0.5,1,0.5),entity,false);

    }


}
