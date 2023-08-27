package dev.tablesalt.dungeon.item.impl.weapon;

import dev.tablesalt.dungeon.database.DungeonCache;
import dev.tablesalt.dungeon.database.EnchantableItem;
import dev.tablesalt.dungeon.item.ItemAttribute;
import dev.tablesalt.dungeon.item.Rarity;
import dev.tablesalt.dungeon.item.Tier;
import dev.tablesalt.dungeon.model.TBSSound;
import dev.tablesalt.dungeon.util.TBSItemUtil;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.mineacademy.fo.RandomUtil;

import java.util.List;

public final class PickpocketAttribute extends ItemAttribute {

    @Getter
    private static final PickpocketAttribute instance = new PickpocketAttribute();

    @Getter
    private final Rarity rarity;

    private PickpocketAttribute() {
        super();

        this.rarity = Rarity.COMMON;
    }

    @Override
    public void onPvP(Player attacker, Player victim, EnchantableItem item, Tier tier, EntityDamageByEntityEvent event) {
        DungeonCache cache = DungeonCache.from(attacker);

        int playSoundRandomly = RandomUtil.nextBetween(0, 2);
        if (playSoundRandomly == 0)
            TBSSound.MoneyPickup.getInstance().playTo(attacker);

        cache.giveMoney(goldPerHit(tier));
    }

    @Override
    public String getName() {
        return "&6Pickpocket";
    }

    @Override
    public List<String> getAttributeLore(Tier tier) {
        return List.of(new String[]{" ",
                TBSItemUtil.makeItemTitle(getName() + " " + tier.getAsRomanNumeral()),
                rarity.getFormattedName() + "&7Every hit on a player",
                "&7gains you &b" + goldPerHit(tier) + " &7gold.",
                " "
        });
    }

    private double goldPerHit(Tier tier) {
        if (tier == Tier.ONE)
            return 5;

        if (tier == Tier.TWO)
            return 8;

        if (tier == Tier.THREE)
            return 10;

        return 3;
    }

    @Override
    public boolean isForArmor() {
        return false;
    }
}
