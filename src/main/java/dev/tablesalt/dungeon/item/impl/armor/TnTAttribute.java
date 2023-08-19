package dev.tablesalt.dungeon.item.impl.armor;

import dev.tablesalt.dungeon.item.ItemAttribute;
import dev.tablesalt.dungeon.item.Rarity;
import dev.tablesalt.dungeon.item.Tier;
import dev.tablesalt.dungeon.util.TBSItemUtil;
import lombok.Getter;

import java.util.List;

public final class TnTAttribute extends ItemAttribute {

    @Getter
    private static final TnTAttribute instance = new TnTAttribute();

    @Getter
    private final Rarity rarity;

    private TnTAttribute() {
        super();

        this.rarity = Rarity.MYTHIC;
    }

    @Override
    public String getName() {
        return "TNT";
    }

    @Override
    public List<String> getAttributeLore(Tier tier) {
        return List.of(new String[]{" ",
                TBSItemUtil.makeItemTitle(getName() + " " + tier.getAsRomanNumeral()),
                rarity.getFormattedName() + "&7Work in progress",
                " "
        });
    }

    @Override
    public boolean isForArmor() {
        return true;
    }
}
