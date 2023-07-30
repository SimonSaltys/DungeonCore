package dev.tablesalt.dungeon.commands;

import dev.tablesalt.dungeon.database.DungeonCache;
import dev.tablesalt.dungeon.database.EnchantableItem;
import dev.tablesalt.dungeon.item.Tier;
import dev.tablesalt.dungeon.util.PlayerUtil;
import dev.tablesalt.gamelib.commands.GameSubCommand;
import org.bukkit.Material;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.*;

public final class GiveEnchantableItem extends GameSubCommand {
    private GiveEnchantableItem() {
        super("give", 0, "", "adds attribute to held item");
    }

    @Override
    protected void onCommand() {

        DungeonCache cache = DungeonCache.from(getPlayer());
        cache.giveMoney(10000);
        Common.broadcast("Money " + cache.getMoney());


        EnchantableItem enchantableItem = new EnchantableItem(
                CompMaterial.LEATHER_CHESTPLATE.name(),
                Material.LEATHER_CHESTPLATE,
                new HashMap<>(),
                Tier.NONE,
                UUID.randomUUID());

        PlayerUtil.giveItem(getPlayer(),enchantableItem.compileToItemStack());
    }
}
