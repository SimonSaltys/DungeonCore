package dev.tablesalt.dungeon.util;

import dev.tablesalt.dungeon.database.DungeonCache;
import dev.tablesalt.dungeon.database.EnchantableItem;
import dev.tablesalt.dungeon.item.impl.AttributeTestOne;
import dev.tablesalt.dungeon.item.impl.Tier;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.ItemUtil;
import org.mineacademy.fo.remain.CompMetadata;

import java.util.UUID;

@UtilityClass
public class TBSItemUtil {
    /**
     * Used for formatting attribute titles
     * on items
     */
    public String makeItemTitle(String title) {
        return "&9&l" + title;
    }

    /**
     * Enchants the item by increasing its tier
     * applies a random attribute and
     * increases the tier of other random attributes
     */
    public ItemStack enchantItem(EnchantableItem item) {
        item.addAttribute(AttributeTestOne.getInstance(), Tier.TWO);

        return item.compileToItemStack();
    }

    public UUID getItemsUUID(ItemStack item) {
        String uuidString = CompMetadata.getMetadata(item,"UUID");

        if (uuidString == null)
            return null;

        return UUID.fromString(uuidString);
    }

    /**
     * returns true if the item can
     * be enchanted in the enchanting menu
     */
    public boolean isEnchantable(Player player, ItemStack item) {
        DungeonCache cache = DungeonCache.from(player);

        UUID itemsUUID = getItemsUUID(item);
        if (itemsUUID != null)
            for (EnchantableItem enchantableItem : cache.getEnchantableItems())
                 if (enchantableItem.getUuid().equals(itemsUUID))
                    return true;


       return false;
    }
}
