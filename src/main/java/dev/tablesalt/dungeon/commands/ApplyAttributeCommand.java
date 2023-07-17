package dev.tablesalt.dungeon.commands;

import dev.tablesalt.dungeon.database.EnchantableItem;
import dev.tablesalt.dungeon.database.RedisDatabase;
import dev.tablesalt.dungeon.item.ItemAttribute;
import dev.tablesalt.dungeon.item.impl.AttributeTestOne;
import dev.tablesalt.dungeon.item.impl.Tier;
import dev.tablesalt.dungeon.menu.enchanting.EnchantingMenu;
import dev.tablesalt.dungeon.util.ItemUtil;
import dev.tablesalt.gamelib.commands.GameSubCommand;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.CompMetadata;

import java.io.IOException;
import java.util.*;

public final class ApplyAttributeCommand extends GameSubCommand {
    private ApplyAttributeCommand() {
        super("increase", 0, "", "adds attribute to held item");
    }

    @Override
    protected void onCommand() {

        HashMap<ItemAttribute,Integer> map = new HashMap<>();
        map.put(AttributeTestOne.getInstance(),2);

        EnchantableItem item = new EnchantableItem("Some name", Material.IRON_AXE,
                map, Tier.THREE, UUID.randomUUID());

        ItemStack compiledItem = item.compileToItemStack();

        getPlayer().getInventory().setItemInMainHand(compiledItem);

        Common.broadcast(CompMetadata.getMetadata(compiledItem,"attribute_1") + "");

    }
}
