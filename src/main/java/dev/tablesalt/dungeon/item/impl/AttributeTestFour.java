package dev.tablesalt.dungeon.item.impl;

import dev.tablesalt.dungeon.item.ItemAttribute;
import dev.tablesalt.dungeon.item.Rarity;
import dev.tablesalt.dungeon.item.Tier;
import dev.tablesalt.dungeon.util.TBSItemUtil;
import lombok.Getter;

import java.util.List;

public final class AttributeTestFour extends ItemAttribute {

    @Getter
    private static final AttributeTestFour instance = new AttributeTestFour();

    @Getter
    private final Rarity rarity;

    private AttributeTestFour() {
        super();

        this.rarity = Rarity.RARE;
    }

    @Override
    public String getName() {
        return "Attribute Four";
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
