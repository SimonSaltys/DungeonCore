package dev.tablesalt.dungeon.item.impl;

import dev.tablesalt.dungeon.item.ItemAttribute;
import dev.tablesalt.dungeon.util.TBSItemUtil;
import lombok.Getter;

import java.util.List;

public final class AttributeTestOne extends ItemAttribute {

    @Getter
    private static final AttributeTestOne instance = new AttributeTestOne();

    @Getter
    private final Rarity rarity;

    private AttributeTestOne() {
        super();

        this.rarity = Rarity.COMMON;
    }

    @Override
    public String getName() {
        return "Attribute One";
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
