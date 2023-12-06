package dev.tablesalt.dungeon.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.jna.platform.win32.COM.util.annotation.ComObject;
import dev.tablesalt.dungeon.DungeonPlugin;
import dev.tablesalt.dungeon.database.DungeonCache;
import dev.tablesalt.dungeon.database.EnchantableItem;
import dev.tablesalt.dungeon.item.ItemAttribute;
import dev.tablesalt.dungeon.item.ItemType;
import dev.tablesalt.dungeon.item.Rarity;
import dev.tablesalt.dungeon.item.Tier;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.core.util.JsonUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.FileUtil;
import org.mineacademy.fo.RandomUtil;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.metrics.Metrics;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.CompMetadata;

import java.io.*;
import java.nio.charset.StandardCharsets;
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


    /**
     * Enchants the item by increasing its tier
     * applies a random attribute and
     * increases the tier of other random attributes
     */
    public EnchantableItem enchantItem(EnchantableItem item) {

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

        return item;
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

            //will never be empty, just checking and handling for safety
            if (!attributesToChoose.isEmpty()) {
                ItemAttribute chosenAttribute = RandomUtil.nextItem(attributesToChoose);
                item.addAttribute(chosenAttribute, Tier.NONE);
            }

        }
    }

    private List<ItemAttribute> getAttributesToChoose(Rarity rarity, EnchantableItem item) {
        List<ItemAttribute> attributesToChoose = ItemAttribute.getAttributesOfRarity(rarity);

        //remove the attributes that already are on the item... if any...
        attributesToChoose.removeIf(item.getAttributeTierMap().keySet()::contains);

        ItemType itemType = getItemCategory(item.getMaterial());

        //remove the armor attributes if the item is a weapon
        if (itemType == ItemType.ARMOR)
            attributesToChoose.removeIf(attribute -> attribute.getType() != ItemType.ARMOR);
        else if (itemType == ItemType.WEAPON)
            attributesToChoose.removeIf(attribute -> attribute.getType() != ItemType.WEAPON);


        return attributesToChoose;
    }

    private void upgradeRandomAttribute(EnchantableItem item) {
        List<ItemAttribute> attributes = new ArrayList<>();

        //let's get all current attributes on the item
        for (ItemAttribute attribute : item.getAttributeTierMap().keySet()) {
            if (attribute != null && item.getAttributeTierMap().get(attribute) < 3)
                attributes.add(attribute);
        }

        if (attributes.isEmpty()) return;

        //select a random attribute to upgrade
        ItemAttribute attributeToUpgrade = RandomUtil.nextItem(attributes);
        Integer tier = item.getAttributeTierMap().get(attributeToUpgrade);

        //do some trickery to upgrade the attribute
        item.getAttributeTierMap().remove(attributeToUpgrade);
        item.getAttributeTierMap().put(attributeToUpgrade, tier + 1);
    }

    /**
     * Will return the itemstacks uuid,
     * this will tell you if the item has
     * {@link ItemAttribute}s on it since all
     * enchant-able items will have a UUID
     * @param item
     * @return
     */
    public UUID getItemsUUID(ItemStack item) {
        if (item == null)
            return  null;

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


    private Map<String,List<String>> itemTypes = new HashMap<>();

    /**
     * Gets the category of item this
     * material falls under.
     * <p>
     *
     * see {@link ItemType} for an enum for all possible types
     */
    public ItemType getItemCategory(Material material) {
       if (itemTypes.isEmpty())
           loadFile();

        return getItemType(material);
    }

    private void loadFile() {
        try {
            InputStream typesStream = DungeonPlugin.getInstance().getResource("itemtypes.json");
            Valid.checkNotNull(typesStream,"Could not find file containing item types");

            BufferedReader streamReader = new BufferedReader(new InputStreamReader(typesStream, StandardCharsets.UTF_8));
            Gson gson = new Gson();

            TypeToken<Map<String, List<String>>> token = new TypeToken<>() {
            };

            itemTypes = gson.fromJson(streamReader, token.getType());

        } catch (Exception ex) {
            Common.error(ex,"Could not parse item type file");
        }
    }

    private ItemType getItemType(Material material) {
        for (Map.Entry<String, List<String>> entry : itemTypes.entrySet()) {
            String category = entry.getKey();
            List<String> itemsInCategory = entry.getValue();

            if (itemsInCategory.contains(material.name())) {
                // The material is in this category, you can return the category here.
                return ItemType.valueOf(category);
            }
        }
        return ItemType.NONE;
    }

    /**
     * This helps map itemstacks to what equipment slot they correspond to.
     * This is useful when dealing with the players armor contents inventory
     */
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

        /**
         *Returns the first itemstack that matches the equipment slot provided.
         */
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

        public EquipmentSlot getEquipmentSlot(ItemStack stack) {
            String itemName = stack.getType().name();

            for (EquipmentSlot slot : slotToArmor.keySet()) {
                String slotName = slotToArmor.get(slot);

                if (itemName.contains(slotName))
                    return slot;
            }

            return null;
        }
    }
}
