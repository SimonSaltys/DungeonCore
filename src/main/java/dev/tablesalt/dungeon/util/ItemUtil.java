package dev.tablesalt.dungeon.util;

import dev.tablesalt.dungeon.database.DungeonCache;
import dev.tablesalt.dungeon.database.EnchantableItem;
import dev.tablesalt.dungeon.item.AttributeActions;
import dev.tablesalt.dungeon.item.ItemAttribute;
import dev.tablesalt.dungeon.item.impl.AttributeTestOne;
import dev.tablesalt.dungeon.item.impl.Tier;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMetadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class ItemUtil {
    /**
     * Used for formatting attribute titles
     * on items
     */
    public String makeItemTitle(String title) {
        return "&9&l" + org.mineacademy.fo.ItemUtil.bountifyCapitalized(title);
    }

    /**
     * Enchants the item by increasing its tier
     * applies a random attribute and
     * increases the tier of other random attributes
     */
    public ItemStack enchantItem(ItemStack item) {
        return item;
    }

    /**
     * returns true if the item can
     * be enchanted in the enchanting menu
     */
    public boolean isEnchantable(ItemStack item) {
        EnchantableItem enchantableItem = fromItemStack(item);

        if (enchantableItem == null)
            return false;

        return (item.getType().equals(Material.LEATHER_CHESTPLATE) || item.getType().equals(Material.GOLDEN_SWORD)
                || item.getType().equals(Material.BOW)) && (enchantableItem.getCurrentTier().getAsInteger() < 3);
    }


    public EnchantableItem fromItemStack(ItemStack itemStack) {

        String uuid = CompMetadata.getMetadata(itemStack,"UUID");


        return null;
    }



}
