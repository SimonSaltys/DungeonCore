package dev.tablesalt.dungeon.collection;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.entity.Player;
import org.mineacademy.fo.model.SimpleTime;

import java.util.HashMap;

public class Cooldown {


    private final HashMap<Player, CooldownTimePair> cooldowns;

    public Cooldown() {
        cooldowns = new HashMap<>();
    }

    public boolean hasTimeLeft(Player player) {

        if (cooldowns.containsKey(player)) {
            long cooldownEndTime = cooldowns.get(player).getCurrentTime() + cooldowns.get(player).getDuration();
            long currentTime = System.currentTimeMillis();
            return cooldownEndTime > currentTime;
        }
        return false;
    }

    public int getSecondsLeft(Player player) {
        if (cooldowns.containsKey(player)) {
            long cooldownEndTime = cooldowns.get(player).getCurrentTime() + cooldowns.get(player).getDuration();
            long currentTime = System.currentTimeMillis();
            return (int) Math.max(0, (cooldownEndTime - currentTime) / 1000);
        }
        return 0;
    }

    public void startCooldown(Player player, SimpleTime time) {
        cooldowns.put(player, new CooldownTimePair(System.currentTimeMillis(), time.getTimeMilliseconds()));
    }


    @Data
    @AllArgsConstructor
    private static class CooldownTimePair {
        private long currentTime;

        private long duration;
    }
}
