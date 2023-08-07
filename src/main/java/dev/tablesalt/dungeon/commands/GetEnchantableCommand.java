package dev.tablesalt.dungeon.commands;

import dev.tablesalt.dungeon.database.EnchantableItem;
import dev.tablesalt.dungeon.item.Tier;
import dev.tablesalt.dungeon.util.PlayerUtil;
import dev.tablesalt.gamelib.commands.GameSubCommand;
import org.bukkit.Material;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.HashMap;
import java.util.UUID;

public final class GetEnchantableCommand extends GameSubCommand {
    private GetEnchantableCommand() {
        super("enchantable", 0, "", "adds attribute to held item");
    }

    @Override
    protected void onCommand() {

        EnchantableItem enchantableItem = new EnchantableItem(
                CompMaterial.LEATHER_CHESTPLATE.name(),
                Material.LEATHER_CHESTPLATE,
                new HashMap<>(),
                Tier.NONE,
                UUID.randomUUID());

        PlayerUtil.giveItem(getPlayer(), enchantableItem.compileToItemStack());

    }
}
