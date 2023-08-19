package dev.tablesalt.dungeon.item.impl.weapon;

import dev.tablesalt.dungeon.item.ItemAttribute;
import dev.tablesalt.dungeon.item.Rarity;
import dev.tablesalt.dungeon.item.Tier;
import dev.tablesalt.dungeon.util.TBSItemUtil;
import lombok.Getter;

import java.util.List;

public final class CursedRetaliationAttribute extends ItemAttribute {
    @Getter
    private static final CursedRetaliationAttribute instance = new CursedRetaliationAttribute();

    @Getter
    private final Rarity rarity;

    private CursedRetaliationAttribute() {
        super();

        this.rarity = Rarity.EPIC;
    }

    @Override
    public String getName() {
        return "&cCursed Retaliation";
    }

    @Override
    public List<String> getAttributeLore(Tier tier) {
        return List.of(new String[]{" ",
                TBSItemUtil.makeItemTitle(getName() + " " + tier.getAsRomanNumeral()),
                rarity.getFormattedName() + "&7When striking an enemy",
                "&7there is a {chance}% chance to apply a curse",
                "&7that deals {damage} every {time unit}"
        });
    }

    @Override
    public boolean isForArmor() {
        return false;
    }
}
