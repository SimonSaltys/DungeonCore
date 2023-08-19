package dev.tablesalt.dungeon.item.impl.armor;

import dev.tablesalt.dungeon.item.ItemAttribute;
import dev.tablesalt.dungeon.item.Rarity;
import dev.tablesalt.dungeon.item.Tier;
import dev.tablesalt.dungeon.util.TBSItemUtil;
import lombok.Getter;

import java.util.List;

public class EtherealDodgeAttribute extends ItemAttribute {

    @Getter
    private static final EtherealDodgeAttribute instance = new EtherealDodgeAttribute();

    @Getter
    private final Rarity rarity;

    private EtherealDodgeAttribute() {
        this.rarity = Rarity.MYTHIC;
    }

    @Override
    public String getName() {
        return "&eEthereal Dodge";
    }

    @Override
    public List<String> getAttributeLore(Tier tier) {
        return List.of(new String[]{" ",
                TBSItemUtil.makeItemTitle(getName() + " " + tier.getAsRomanNumeral()),
                rarity.getFormattedName() + "&7When you are hit",
                "&7every {time unit}",
                "&7you will phase out of reality",
                "&7for {time unit} "
        });
    }

    @Override
    public boolean isForArmor() {
        return true;
    }
}
