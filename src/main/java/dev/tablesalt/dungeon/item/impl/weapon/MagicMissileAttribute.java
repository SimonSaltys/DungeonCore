package dev.tablesalt.dungeon.item.impl.weapon;

import dev.tablesalt.dungeon.item.ItemAttribute;
import dev.tablesalt.dungeon.item.ItemType;
import dev.tablesalt.dungeon.item.Rarity;
import dev.tablesalt.dungeon.item.Tier;
import dev.tablesalt.dungeon.util.TBSItemUtil;
import lombok.Getter;

import java.util.List;

public final class MagicMissileAttribute extends ItemAttribute {

    @Getter
    private static final MagicMissileAttribute instance = new MagicMissileAttribute();

    @Getter
    private final Rarity rarity;

    private MagicMissileAttribute() {
        super();

        this.rarity = Rarity.EPIC;
    }

    @Override
    public String getName() {
        return "&dMagic Missile";
    }

    @Override
    public List<String> getAttributeLore(Tier tier) {
        return List.of(new String[]{" ",
                TBSItemUtil.makeItemTitle(getName() + " " + tier.getAsRomanNumeral()),
                rarity.getFormattedName() + "&7work in progress",
                " "
        });
    }

    @Override
    public ItemType getType() {
        return ItemType.WEAPON;
    }
}
