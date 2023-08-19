package dev.tablesalt.dungeon.item.impl.weapon;

import dev.tablesalt.dungeon.item.ItemAttribute;
import dev.tablesalt.dungeon.item.Rarity;
import dev.tablesalt.dungeon.item.Tier;
import dev.tablesalt.dungeon.util.TBSItemUtil;
import lombok.Getter;

import java.util.List;

/**
 * rewards? player for hitting 3+ consecutive hits on target
 */
public final class FullComboAttribute extends ItemAttribute {

    @Getter
    private static final FullComboAttribute instance = new FullComboAttribute();

    @Getter
    private final Rarity rarity;

    private FullComboAttribute() {
        super();

        this.rarity = Rarity.RARE;
    }

    @Override
    public String getName() {
        return "&bFull Combo";
    }

    @Override
    public List<String> getAttributeLore(Tier tier) {
        return List.of(new String[]{" ",
                TBSItemUtil.makeItemTitle(getName() + " " + tier.getAsRomanNumeral()),
                rarity.getFormattedName() + "&7A Simple attribute that does nothing",
                "&7but add &b" + (tier.getAsInteger() + 5) + " &7of some stat!",
                " "
        });
    }

    @Override
    public boolean isForArmor() {
        return false;
    }
}
