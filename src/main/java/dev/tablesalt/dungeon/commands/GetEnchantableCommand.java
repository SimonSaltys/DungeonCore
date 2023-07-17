package dev.tablesalt.dungeon.commands;

import dev.tablesalt.dungeon.database.EnchantableItem;
import dev.tablesalt.dungeon.item.ItemAttribute;
import dev.tablesalt.dungeon.item.impl.AttributeTestOne;
import dev.tablesalt.dungeon.item.impl.Tier;
import dev.tablesalt.gamelib.commands.GameSubCommand;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;

import java.util.HashMap;
import java.util.UUID;

public final class GetEnchantableCommand extends GameSubCommand {
    private GetEnchantableCommand() {
        super("enchantable", 0, "", "adds attribute to held item");
    }

    @Override
    protected void onCommand() {

       ItemStack item = getPlayer().getInventory().getItemInMainHand();

       EnchantableItem enchantableItem = EnchantableItem.getFromItemStack(item);

       if (enchantableItem != null)
           tellSuccess("Valid! " + enchantableItem);
       else
           tellError("Invalid.");

    }
}
