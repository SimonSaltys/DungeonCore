package dev.tablesalt.dungeon.item.impl;

import dev.tablesalt.dungeon.item.ItemAttribute;
import dev.tablesalt.dungeon.item.Rarity;
import dev.tablesalt.dungeon.item.Tier;
import dev.tablesalt.dungeon.util.TBSItemUtil;
import lombok.Getter;

import java.util.List;

public final class AttributeTestThree extends ItemAttribute {

    @Getter
    private static final AttributeTestThree instance = new AttributeTestThree();

    @Getter
    private final Rarity rarity;

    private AttributeTestThree() {
        super();

        this.rarity = Rarity.EPIC;
    }

    @Override
    public String getName() {
        return "Attribute Three";
    }

    @Override
    public List<String> getAttributeLore(Tier tier) {
        return List.of(new String[]{ " ",
                TBSItemUtil.makeItemTitle(getName() + " " + tier.getAsRomanNumeral()),
                rarity.getFormattedName() + "&7A Simple attribute that does nothing",
                "&7but add &b" + (tier.getAsInteger() + 5) + " &7of some stat!",
                " "
        });
    }



}
