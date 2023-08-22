package dev.tablesalt.dungeon.commands;

import dev.tablesalt.dungeon.database.EnchantableItem;
import dev.tablesalt.dungeon.item.ItemAttribute;
import dev.tablesalt.dungeon.item.Tier;
import dev.tablesalt.dungeon.util.TBSItemUtil;
import dev.tablesalt.gamelib.commands.GameSubCommand;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class GetEnchantableCommand extends GameSubCommand {
    private GetEnchantableCommand() {
        super("enchant", 3, "<weapon|armor> <tier> <enchant>", "gives item with specified enchant or adds to that held item.");
    }

    @Override
    protected void onCommand() {
        checkConsole();

        Player player = getPlayer();

        String weaponOrArmor = args[0];

        String stringTier = args[1];

        String enchantName = joinArgs(2);

        ItemAttribute attributeToApply = ItemAttribute.fromName(enchantName);
        
        if (attributeToApply == null) {
            tellError("Could not find enchant by the name " + enchantName);
            return;
        }

        ItemStack itemInHand = player.getInventory().getItemInMainHand();


        if (!TBSItemUtil.isEnchantable(itemInHand) && itemInHand.getType() != Material.AIR) {
            tellError("The item you are holding cannot be enchanted.");
            return;
        }


        if (weaponOrArmor.equalsIgnoreCase("Weapon")) {
            if (attributeToApply.isForArmor()) {
                tellError("This enchantment cannot be applied to weapons.");
                return;
            }
        } else if (weaponOrArmor.equalsIgnoreCase("Armor")) {
            if (!attributeToApply.isForArmor()) {
                tellError("This enchantment cannot be applied to armor.");
                return;
            }
        }

        Tier tier = Tier.NONE;
        try {
            tier = Tier.fromInteger(Integer.parseInt(stringTier));

        } catch (NumberFormatException e) {
            tellError("Invalid number for tier, must be between 0-3");
        }

        if (itemInHand.getType() != Material.AIR) {
            EnchantableItem enchantableItem = EnchantableItem.fromItemStack(itemInHand);
            enchantableItem.addAttribute(ItemAttribute.fromName(enchantName), tier);
            player.getInventory().setItemInMainHand(enchantableItem.compileToItemStack());
        } else {

            HashMap<ItemAttribute, Integer> attributeTierHashMap = new HashMap<>();
            attributeTierHashMap.put(ItemAttribute.fromName(enchantName), tier.getAsInteger());

            EnchantableItem enchantableItem = new EnchantableItem(UUID.randomUUID(), "Custom Item",
                    (weaponOrArmor.equalsIgnoreCase("Weapon") ? Material.GOLDEN_SWORD : Material.LEATHER_CHESTPLATE),
                    attributeTierHashMap, Tier.NONE);

            player.getInventory().setItemInMainHand(enchantableItem.compileToItemStack());
        }

    }


    @Override
    protected List<String> tabComplete() {
        if (args.length == 1)
            return Arrays.asList("Weapon", "Armor");

        if (args.length == 2)
            return completeLastWord(0, 1, 2, 3);

        if (args.length == 3)
            return Common.convert((args[0].equalsIgnoreCase("armor")
                            ? ItemAttribute.getRegisteredAttributes().stream().filter(ItemAttribute::isForArmor).toList() :
                            ItemAttribute.getRegisteredAttributes().stream().filter(attribute -> !attribute.isForArmor()).toList()),
                    attribute -> Common.stripColors(attribute.getName()));

        return NO_COMPLETE;
    }
}
