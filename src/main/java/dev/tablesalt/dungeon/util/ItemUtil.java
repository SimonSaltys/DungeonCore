package dev.tablesalt.dungeon.util;

import dev.tablesalt.dungeon.item.impl.Tier;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.menu.model.ItemCreator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class ItemUtil {

    public ItemStack increaseTier(@NonNull ItemStack itemStack) {
        return makeItem(Tier.getNext(getTier(itemStack)).getAsInteger(),itemStack);
    }

    public boolean isEnchantable(ItemStack item) {
        if (item == null)
            return false;

        return (item.getType().equals(Material.LEATHER_CHESTPLATE) || item.getType().equals(Material.GOLDEN_SWORD)
                || item.getType().equals(Material.BOW)) && (getTier(item).getAsInteger() < 3);
    }



    private ItemStack makeItem(int tier, ItemStack itemStack) {
        return ItemCreator.of(itemStack)
                .name((tier > 0 && tier < 4 ? "Tier " + Tier.fromInteger(tier).getAsRomanNumeral() + " " : "")
                + org.mineacademy.fo.ItemUtil.bountifyCapitalized(itemStack.getType().name())).make();
    }

    private Tier getTier(@NonNull ItemStack itemStack) {
        String name = itemStack.getItemMeta().getDisplayName();

        Pattern pattern = Pattern.compile("\\b(?=[MDCLXVI])M*D?C{0,4}L?X{0,4}V?I{0,4}\\b");
        Matcher matcher = pattern.matcher(name);

        if (matcher.find())
            return Tier.fromRomanNumeral(matcher.group());

        return Tier.NONE;
    }



}
