package dev.tablesalt.dungeon.item.impl.weapon;

import dev.tablesalt.dungeon.database.EnchantableItem;
import dev.tablesalt.dungeon.event.PlayerGainGoldEvent;
import dev.tablesalt.dungeon.item.ItemAttribute;
import dev.tablesalt.dungeon.item.ItemType;
import dev.tablesalt.dungeon.item.Rarity;
import dev.tablesalt.dungeon.item.Tier;
import dev.tablesalt.dungeon.util.TBSItemUtil;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.List;

public final class GoldRushAttribute extends ItemAttribute {

    @Getter
    private static final GoldRushAttribute instance = new GoldRushAttribute();

    @Getter
    private final Rarity rarity;


    private GoldRushAttribute() {
        super();

        this.rarity = Rarity.COMMON;
    }

    @Override
    public void onGoldGain(Player player, EnchantableItem item, double amountGained, Tier tier, PlayerGainGoldEvent event) {
        double percentageIncrease = getGainsAsPercent(tier);
        double additionalGold = amountGained * (percentageIncrease / 100.0);

        event.addMoreGold(additionalGold);
    }

    @Override
    public String getName() {
        return "&6Gold Rush";
    }

    @Override
    public List<String> getAttributeLore(Tier tier) {
        return List.of(new String[]{" ",
                TBSItemUtil.makeItemTitle(getName() + " " + tier.getAsRomanNumeral()),
                rarity.getFormattedName() + "&7Increases gold gains",
                "by &b" + getGainsAsPercent(tier) + "&7 percent",
                " "
        });
    }

    public int getGainsAsPercent(Tier tier) {
        if (tier == Tier.ONE)
            return 3;

        if (tier == Tier.TWO)
            return 6;

        if (tier == Tier.THREE)
            return 9;

        return 2;
    }

    @Override
    public ItemType getType() {
        return ItemType.WEAPON;
    }


}
