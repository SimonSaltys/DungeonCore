package dev.tablesalt.dungeon.util;

import dev.tablesalt.dungeon.database.DungeonCache;
import dev.tablesalt.dungeon.database.EnchantableItem;
import dev.tablesalt.dungeon.item.ItemAttribute;
import dev.tablesalt.dungeon.item.Rarity;
import dev.tablesalt.dungeon.item.Tier;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.RandomUtil;
import org.mineacademy.fo.menu.model.ItemCreator;
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
    public ItemStack enchantItem(Player player, EnchantableItem item) {
        DungeonCache cache = DungeonCache.from(player);

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

        cache.updateEnchantableItem(item);

        return item.compileToItemStack();
    }

    private void applyRandomAttribute(EnchantableItem item) {
        Rarity rarity = Rarity.getRandomWeighted();
        List<ItemAttribute> attributesToChoose = getAttributesToChoose(rarity, item);

        //choose and set the randomly selected attribute
        if (!attributesToChoose.isEmpty()) {
            ItemAttribute chosenAttribute = RandomUtil.nextItem(attributesToChoose);
            item.addAttribute(chosenAttribute, Tier.NONE);
        } else {
            //could not find anymore attributes of this rarity, choosing a different rarity
            Rarity nextRarity = Rarity.getRandomWeighted(rarity);
            attributesToChoose = getAttributesToChoose(nextRarity, item);

            //will never be empty, just checking for safety
            if (!attributesToChoose.isEmpty()) {
                ItemAttribute chosenAttribute = RandomUtil.nextItem(attributesToChoose);
                item.addAttribute(chosenAttribute, Tier.NONE);
            }

        }
    }

    private List<ItemAttribute> getAttributesToChoose(Rarity rarity, EnchantableItem item) {
        List<ItemAttribute> attributesToChoose = ItemAttribute.getAttributesOfRarity(rarity);
        attributesToChoose.removeIf(item.getAttributeTierMap().keySet()::contains);

        //remove the armor attributes if the item is a weapon
        if (isArmor(item.getMaterial()))
            attributesToChoose.removeIf(attribute -> !attribute.isForArmor());
        else
            attributesToChoose.removeIf(attribute -> attribute.isForArmor());


        return attributesToChoose;
    }

    private void upgradeRandomAttribute(EnchantableItem item) {
        List<ItemAttribute> attributes = new ArrayList<>();

        for (ItemAttribute attribute : item.getAttributeTierMap().keySet())
            if (item.getAttributeTierMap().get(attribute) < 3)
                attributes.add(attribute);

        if (attributes.isEmpty()) return;

        ItemAttribute attributeToUpgrade = RandomUtil.nextItem(attributes);
        Integer tier = item.getAttributeTierMap().get(attributeToUpgrade);

        item.getAttributeTierMap().remove(attributeToUpgrade);
        item.getAttributeTierMap().put(attributeToUpgrade, tier + 1);
    }

    public UUID getItemsUUID(ItemStack item) {
        String uuidString = CompMetadata.getMetadata(item, "UUID");

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

    public boolean isArmor(Material material) {
        String itemMaterialName = material.name();
        String[] armorTypes = new String[]{"HELMET", "CHESTPLATE", "LEGGINGS", "BOOTS", "TUNIC"};

        for (String name : armorTypes)
            if (itemMaterialName.contains(name))
                return true;

        return false;
    }


    public final class ArmorSlotMapper {
        private static final Map<EquipmentSlot, String> slotToArmor = new HashMap<>();

        @Getter
        private static final ArmorSlotMapper instance = new ArmorSlotMapper();

        private ArmorSlotMapper() {
            slotToArmor.put(EquipmentSlot.HEAD, "HELMET");
            slotToArmor.put(EquipmentSlot.CHEST, "CHESTPLATE");
            slotToArmor.put(EquipmentSlot.LEGS, "LEGGINGS");
            slotToArmor.put(EquipmentSlot.FEET, "BOOTS");
        }

        public ItemStack getItemStackTypeInArmor(ItemStack[] itemsToCheck, EquipmentSlot slot) {
            for (ItemStack item : itemsToCheck) {
                if (item == null)
                    continue;

                String itemName = item.getType().name();
                String slotName = slotToArmor.get(slot);
                if (itemName.contains(slotName)) {
                    return item;
                }
            }
            return ItemCreator.of(CompMaterial.AIR).make();
        }
    }
}
