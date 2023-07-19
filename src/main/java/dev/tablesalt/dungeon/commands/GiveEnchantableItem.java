package dev.tablesalt.dungeon.commands;

import dev.tablesalt.dungeon.database.DungeonCache;
import dev.tablesalt.dungeon.database.EnchantableItem;
import dev.tablesalt.dungeon.item.ItemAttribute;
import dev.tablesalt.dungeon.item.impl.AttributeTestOne;
import dev.tablesalt.dungeon.item.impl.Tier;
import dev.tablesalt.dungeon.menu.enchanting.EnchantingMenu;
import dev.tablesalt.dungeon.util.PlayerUtil;
import dev.tablesalt.dungeon.util.TBSItemUtil;
import dev.tablesalt.gamelib.commands.GameSubCommand;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.ItemUtil;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.*;

public final class GiveEnchantableItem extends GameSubCommand {
    private GiveEnchantableItem() {
        super("give", 0, "", "adds attribute to held item");
    }

    @Override
    protected void onCommand() {
        EnchantableItem enchantableItem = new EnchantableItem(
                CompMaterial.LEATHER_CHESTPLATE.name(),
                Material.LEATHER_CHESTPLATE,
                new HashMap<>(),
                Tier.NONE,
                UUID.randomUUID());

        PlayerUtil.giveItem(getPlayer(),enchantableItem.compileToItemStack());
    }
}
