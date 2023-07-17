package dev.tablesalt.dungeon.item.impl;

import dev.tablesalt.dungeon.item.AttributeActions;
import dev.tablesalt.dungeon.item.ItemAttribute;
import dev.tablesalt.dungeon.util.ItemUtil;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;

import java.util.List;

public final class AttributeTestOne extends ItemAttribute {

    @Getter
    private static final AttributeTestOne instance = new AttributeTestOne();

    private AttributeTestOne() {
        super();
    }

    @Override
    public String getName() {
        return "Attribute One";
    }

    @Override
    public List<String> getAttributeLore(Tier tier) {
        return List.of(new String[]{ " ",
                ItemUtil.makeItemTitle(getName() + " " + tier.getAsRomanNumeral()),
                "&7A Simple attribute that does nothing",
                "&7but add &b" + (tier.getAsInteger() + 5) + " &7of some stat!",
                " "
        });
    }



}
