package dev.tablesalt.dungeon.util;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Transformation;
import org.mineacademy.fo.ChatUtil;
import org.mineacademy.fo.RandomUtil;
import org.mineacademy.fo.remain.CompChatColor;
import org.mineacademy.fo.remain.Remain;

import java.util.Set;

@UtilityClass
public class EntityUtil {

    public Entity spawnEntity(Location location, EntityType entity, double maxDistanceFromCenter) {

        Location spawnLocation = RandomUtil.nextLocation(location,0.1 ,maxDistanceFromCenter + 0.1,false);

        World worldToSpawn = spawnLocation.getWorld();
       if (worldToSpawn != null)
          return worldToSpawn.spawnEntity(spawnLocation.add(0.5,1,0.5),entity,false);

       return null;
    }

    public TextDisplay createTextDisplay(Location location, String text) {
        TextDisplay display = location.getWorld().spawn(location,TextDisplay.class);

        Transformation transf = display.getTransformation();
        transf.getScale().set(1.2D);

        display.setText(text);

        display.setPersistent(true);
        display.setInvulnerable(true);
        display.setTransformation(transf);
        display.setLineWidth(80);
        display.setBillboard(Display.Billboard.CENTER);
        display.setTextOpacity(Byte.MAX_VALUE);

        return display;
    }

    public TextDisplay getClosestTextDisplay(Location location, double radius) {
        if (location != null)
            for(Entity entity : Remain.getNearbyEntities(location,radius))
                if (entity instanceof TextDisplay textDisplay)
                    return textDisplay;

        return null;
    }


}
