package dev.tablesalt.dungeon.util;

import dev.tablesalt.dungeon.database.EnchantableItem;
import dev.tablesalt.dungeon.item.ItemAttribute;
import dev.tablesalt.dungeon.item.Rarity;
import dev.tablesalt.dungeon.item.Tier;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.RandomUtil;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.CompMetadata;

import java.util.*;

@UtilityClass
public class TBSItemUtil {
    /**
     * Used for formatting attribute titles
     * on items
     */
    public String makeItemTitle(String title) {
        return "&9" + title;
    }


    public EnchantableItem makeEnchantableArmor() {

       return new EnchantableItem(
                CompMaterial.LEATHER_CHESTPLATE.name(),
                Material.LEATHER_CHESTPLATE,
                new HashMap<>(),
                Tier.NONE,
                UUID.randomUUID());
    }

    /**
     * Enchants the item by increasing its tier
     * applies a random attribute and
     * increases the tier of other random attributes
     */
    public ItemStack enchantItem(Player player,EnchantableItem item) {

        int currentTier = item.getCurrentTier().getAsInteger();

        if (currentTier < 3) {
            item.setCurrentTier(Tier.fromInteger(currentTier + 1));

           applyRandomAttribute(item);


            if (currentTier == 1)
                upgradeRandomAttribute(item);

            if (currentTier == 2) {
                for (int i = 0; i < 3; i++)
                    upgradeRandomAttribute(item);
            }
        }
        return item.compileToItemStack();
    }

    private void applyRandomAttribute(EnchantableItem item) {
        Rarity rarity = Rarity.getRandomWeighted();


        List<ItemAttribute> attributesToChoose = ItemAttribute.getAttributesOfRarity(rarity);
        attributesToChoose.removeIf(item.getAttributeTierMap().keySet()::contains);

        //choose and set the randomly selected attribute
        if (!attributesToChoose.isEmpty()) {
            ItemAttribute chosenAttribute = RandomUtil.nextItem(attributesToChoose);
            item.addAttribute(chosenAttribute,Tier.NONE);
        }
    }

    private void upgradeRandomAttribute(EnchantableItem item) {
        List<ItemAttribute> attributes = new ArrayList<>();

        for (ItemAttribute attribute : item.getAttributeTierMap().keySet())
            if (item.getAttributeTierMap().get(attribute) < 3)
                attributes.add(attribute);

        ItemAttribute attributeToUpgrade = RandomUtil.nextItem(attributes);
        Integer tier = item.getAttributeTierMap().get(attributeToUpgrade);

       item.getAttributeTierMap().remove(attributeToUpgrade);
       item.getAttributeTierMap().put(attributeToUpgrade,tier + 1);
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
    public boolean isEnchantable(ItemStack item) {
        if (item == null)
            return false;

        EnchantableItem enchantableItem = EnchantableItem.fromItemStack(item);
        return enchantableItem != null;
    }
}
