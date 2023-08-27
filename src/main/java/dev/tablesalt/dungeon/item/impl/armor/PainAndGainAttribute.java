package dev.tablesalt.dungeon.item.impl.armor;

import dev.tablesalt.dungeon.database.DungeonCache;
import dev.tablesalt.dungeon.database.EnchantableItem;
import dev.tablesalt.dungeon.item.ItemAttribute;
import dev.tablesalt.dungeon.item.Rarity;
import dev.tablesalt.dungeon.item.Tier;
import dev.tablesalt.dungeon.util.TBSItemUtil;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

public final class PainAndGainAttribute extends ItemAttribute {

    @Getter
    private static final PainAndGainAttribute instance = new PainAndGainAttribute();

    @Getter
    private final Rarity rarity = Rarity.COMMON;

    private PainAndGainAttribute() {
    }


    @Override
    public String getName() {
        return "&6Pain And Gain";
    }

    @Override
    public void onDamaged(Player victim, EnchantableItem item, Tier tier, EntityDamageEvent event) {
        DungeonCache cache = DungeonCache.from(victim);

        cache.giveMoney(goldGainedOnHit(tier));
    }

    @Override
    public List<String> getAttributeLore(Tier tier) {
        return List.of(new String[]{" ",
                TBSItemUtil.makeItemTitle(getName() + " " + tier.getAsRomanNumeral()),
                rarity.getFormattedName() + "&7When you get hit",
                "&7you gain &b" + goldGainedOnHit(tier) + " &7gold",
                " "
        });
    }


    private int goldGainedOnHit(Tier tier) {
        if (tier == Tier.ONE)
            return 5;

        if (tier == Tier.TWO)
            return 10;

        if (tier == Tier.THREE)
            return 20;

        return 3;
    }

    @Override
    public boolean isForArmor() {
        return true;
    }
}
