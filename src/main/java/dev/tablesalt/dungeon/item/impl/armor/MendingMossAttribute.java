package dev.tablesalt.dungeon.item.impl.armor;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import dev.tablesalt.dungeon.database.DungeonCache;
import dev.tablesalt.dungeon.item.ItemAttribute;
import dev.tablesalt.dungeon.item.Rarity;
import dev.tablesalt.dungeon.item.Tier;
import dev.tablesalt.dungeon.util.TBSItemUtil;
import dev.tablesalt.gamelib.game.utils.SimpleRunnable;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class MendingMossAttribute extends ItemAttribute {

    @Getter
    private static final MendingMossAttribute instance = new MendingMossAttribute();

    private static final HashMap<Player, RegenRunnable> playersRegenerating = new HashMap<>();


    @Getter
    private final Rarity rarity = Rarity.RARE;

    private MendingMossAttribute() {
    }

    @Override
    public String getName() {
        return "&aMending Moss";
    }

    @Override
    public void onArmorEquip(Player player, Tier tier, PlayerArmorChangeEvent event) {
        playersRegenerating.put(player, new RegenRunnable(tier, DungeonCache.from(player)));
    }

    @Override
    public void onArmorTakeOff(Player player, Tier tier, PlayerArmorChangeEvent event) {
        RegenRunnable regenRunnable = playersRegenerating.get(player);

        if (regenRunnable != null)
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


    private double healthToRegen(Tier tier) {
        if (tier == Tier.ONE)
            return 8;

        if (tier == Tier.TWO)
            return 12;

        if (tier == Tier.THREE)
            return 15;

        return 5;
    }


    private static class RegenRunnable extends SimpleRunnable {

        private final Tier tier;

        private final DungeonCache cache;

        public RegenRunnable(Tier tier, DungeonCache cache) {
            super(-1, 0, 10);
            this.tier = tier;
            this.cache = cache;
        }

        @Override
        protected void onTick() {

            if (!cache.isInCombat()) {
                //todo regen the player play some nice sounds and particles
            }
        }

        @Override
        protected void onEnd() {
            playersRegenerating.remove(cache.toPlayer());
        }
    }


}
