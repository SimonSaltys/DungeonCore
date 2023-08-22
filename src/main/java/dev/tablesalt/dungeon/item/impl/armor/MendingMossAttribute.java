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

    private static final HashMap<DungeonCache, Tier> playersWithAttribute = new HashMap<>();


    private final SimpleRunnable regenRunnable = new RegenRunnable();


    @Getter
    private final Rarity rarity = Rarity.RARE;

    private MendingMossAttribute() {
        if (!regenRunnable.isRunning())
            regenRunnable.launch();
    }

    @Override
    public String getName() {
        return "&aMending Moss";
    }

    @Override
    public void onArmorEquip(Player player, Tier tier, PlayerArmorChangeEvent event) {

    }

    @Override
    public void onArmorTakeOff(Player player, Tier tier, PlayerArmorChangeEvent event) {

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


    private class RegenRunnable extends SimpleRunnable {

        public RegenRunnable() {
            super(-1, 0, 10);
        }

        @Override
        protected void onTick() {
            //todo check if player is not in combat then regen them
        }

        @Override
        protected void onEnd() {

        }
    }


}
