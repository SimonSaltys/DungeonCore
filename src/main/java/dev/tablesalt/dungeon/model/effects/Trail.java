package dev.tablesalt.dungeon.model.effects;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.util.RandomUtils;
import dev.tablesalt.dungeon.util.TBSPlayerUtil;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.mineacademy.fo.RandomUtil;

import java.util.Arrays;
import java.util.List;

public class Trail extends Effect {

    public final Player player;
    public final Particle particle;

    public final List<Color> colors = Arrays.asList(Color.BLACK, Color.SILVER, Color.WHITE);


    public Trail(Player player) {
        super(Effects.getEffectManager());

        this.player = player;
        particle = Particle.REDSTONE;

        type = EffectType.REPEATING;
        period = 1;
        iterations = 150;

        particleOffsetX = -0.2F;
        particleOffsetZ = -0.2F;
        particleOffsetY = 0.2F;
        particleCount = 15;

        color = Color.BLACK;
        setLocation(player.getLocation());
    }

    @Override
    public void onRun() {
        Location location = getLocation();

        location.add(RandomUtils.getRandomCircleVector().multiply(RandomUtils.random.nextDouble() * 0.6d));
        location.add(0, RandomUtils.random.nextFloat() * 2, 0);

        display(particle, location);

        setLocation(player.getLocation());
        color = RandomUtil.nextItem(colors);

        if (TBSPlayerUtil.isOnGround(player))
            cancel();
    }
}
