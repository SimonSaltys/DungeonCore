package dev.tablesalt.dungeon.item.impl.armor;

import dev.tablesalt.dungeon.collection.Cooldown;
import dev.tablesalt.dungeon.database.DungeonCache;
import dev.tablesalt.dungeon.database.EnchantableItem;
import dev.tablesalt.dungeon.item.*;
import dev.tablesalt.dungeon.nms.HealthPackets;
import dev.tablesalt.dungeon.util.TBSItemUtil;
import dev.tablesalt.gamelib.game.utils.SimpleRunnable;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.mineacademy.fo.model.SimpleTime;

import java.util.HashMap;
import java.util.List;

public class MendingMossAttribute extends ItemAttribute implements EnchantmentLifecycle {

    @Getter
    private static final MendingMossAttribute instance = new MendingMossAttribute();

    private static final HashMap<Player, RegenRunnable> playersRegenerating = new HashMap<>();

    private static final Cooldown regenCooldown = new Cooldown();


    @Getter
    private final Rarity rarity = Rarity.RARE;

    private MendingMossAttribute() {
    }

    @Override
    public String getName() {
        return "&aMending Moss";
    }


    @Override
    public void start(Player player, EnchantableItem item, Tier tier) {
        RegenRunnable regenRunnable = new RegenRunnable(tier, DungeonCache.from(player));
        playersRegenerating.put(player, regenRunnable);

        regenRunnable.launch();
    }

    @Override
    public void stop(Player player, EnchantableItem item, Tier tier) {
        RegenRunnable regenRunnable = playersRegenerating.get(player);

        if (regenRunnable != null && regenRunnable.isRunning())
            regenRunnable.end();
    }


    @Override
    public List<String> getAttributeLore(Tier tier) {
        return List.of(new String[]{" ",
                TBSItemUtil.makeItemTitle(getName() + " " + tier.getAsRomanNumeral()),
                rarity.getFormattedName() + "&7When you are out of combat",
                "&7you regen &b" + healthToRegen(tier) / 2 + " &7hearts",
                " "
        });
    }


    private static class RegenRunnable extends SimpleRunnable {

        private final Tier tier;

        private final DungeonCache cache;

        private final Player player;

        private final double healthToRegenPerTick = 0.5;

        private double healthRegained = 0;


        public RegenRunnable(Tier tier, DungeonCache cache) {
            super(-1, 0, 10);
            this.tier = tier;
            this.cache = cache;
            this.player = cache.toPlayer();
        }

        @Override
        protected void onStart() {
        }

        @Override
        protected void onTick() {
            if (!cache.isInCombat() && !regenCooldown.hasTimeLeft(player)) {

                if (healthRegained < healthToRegen(tier)) {

                    double heathGainedOnTick = player.getHealth() + healthToRegenPerTick;

                    //we don't want to overfill the players default max health
                    if (heathGainedOnTick >= 20)
                        return;

                    HealthPackets.sendRegenPacket(player, heathGainedOnTick);
                    player.setHealth(heathGainedOnTick);
                    healthRegained += healthToRegenPerTick;


                    if (healthRegained >= healthToRegen(tier)) {
                        regenCooldown.startCooldown(player, SimpleTime.from("5 seconds"));
                        healthRegained = 0;
                    }
                }
            }
        }

        @Override
        protected void onEnd() {
            playersRegenerating.remove(cache.toPlayer());
            healthRegained = 0;
        }
    }


    private static double healthToRegen(Tier tier) {
        if (tier == Tier.ONE)
            return 6;

        if (tier == Tier.TWO)
            return 8;

        if (tier == Tier.THREE)
            return 10;

        return 3;
    }


    @Override
    public ItemType getType() {
        return ItemType.ARMOR;
    }
}
