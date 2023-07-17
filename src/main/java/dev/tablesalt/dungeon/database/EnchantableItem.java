package dev.tablesalt.dungeon.database;

import dev.tablesalt.dungeon.item.ItemAttribute;
import dev.tablesalt.dungeon.item.impl.Tier;
import dev.tablesalt.dungeon.util.TBSItemUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.ItemUtil;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.exception.FoException;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.model.ConfigSerializable;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.CompMetadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
 @Getter @Setter
public class EnchantableItem implements ConfigSerializable {

     public static final Integer MAX_ENCHANTS_PER_ITEM = 3;

    private String name;

    private Material material;

    private HashMap<ItemAttribute, Integer> attributeTierMap;

    private Tier currentTier;

    private final UUID uuid;

    public EnchantableItem(UUID uuid) {
        this("",Material.AIR, new HashMap<>(),Tier.NONE,uuid);
    }


    public EnchantableItem(String name, Material material, HashMap<ItemAttribute, Integer> attributeTierMap, Tier tier, UUID uuid) {
        this.name = name;
        this.material = material;
        this.attributeTierMap = attributeTierMap;
        this.currentTier = tier;

        this.uuid = uuid;
    }

    @Override
    public SerializedMap serialize() {
        return SerializedMap.ofArray(
                "Name", name,
                "Material", material,
                "Attributes", attributeTierMap,
                "Tier", currentTier,
                "UUID", uuid
        );
    }

     @Override
     public String toString() {
         return "[" + name + ", "
                 + material + ", "
                 + Common.convert(attributeTierMap.keySet(),ItemAttribute::getName) + ", "
                 + currentTier + "]";
     }

     public void addAttribute(ItemAttribute attribute, Tier tier) {
        if (attributeTierMap.containsKey(attribute))
            return;

        attributeTierMap.put(attribute,tier.getAsInteger());
     }

     public ItemStack compileToItemStack() {
        String formattedName = (currentTier != Tier.NONE ? "Tier " + currentTier.getAsRomanNumeral() + " " : "")
                + ItemUtil.bountifyCapitalized(name);

        List<String> lore = new ArrayList<>();

        for (ItemAttribute attribute : attributeTierMap.keySet())
            lore.addAll(attribute.getAttributeLore(Tier.fromInteger(attributeTierMap.get(attribute))));

      ItemStack compiledItem = ItemCreator.of(CompMaterial.fromMaterial(material), formattedName).lore(lore).make();


      return setNBTOnItem(compiledItem);
    }

    private ItemStack setNBTOnItem(ItemStack item) {
        ItemStack nbtTaggedItem;

        nbtTaggedItem = CompMetadata.setMetadata(item,"UUID",uuid.toString());

        int attributeAdded = 0;
        for (ItemAttribute attribute : attributeTierMap.keySet()) {
            attributeAdded++;
            nbtTaggedItem = CompMetadata.setMetadata(nbtTaggedItem,"attribute_" + attributeAdded,
                    SerializedMap.ofArray(
                            "Name",attribute.getName(),
                            "Tier", attributeTierMap.get(attribute)).toJson());
        }
        return nbtTaggedItem;
    }


    public static EnchantableItem getFromItemStack(ItemStack item) {
        String uuidString = CompMetadata.getMetadata(item,"UUID");

        if (uuidString == null)
            return null;

        EnchantableItem enchantableItem = new EnchantableItem(UUID.fromString(uuidString));

        enchantableItem.setMaterial(item.getType());
        enchantableItem.setName(item.getItemMeta().getDisplayName());

        enchantableItem.setCurrentTier(Tier.fromRomanNumeral(enchantableItem.getName()));


        for(int i = 0; i < MAX_ENCHANTS_PER_ITEM; i++) {
            String json = CompMetadata.getMetadata(item,"attribute_" + (i + 1));
            if (json == null)
                continue;

            SerializedMap map = SerializedMap.fromJson(json);
            enchantableItem.getAttributeTierMap().put(
                    ItemAttribute.fromName(map.getString("Name")),
                    map.getInteger("Tier"));
        }

        return enchantableItem;
    }

    public static EnchantableItem deserialize(SerializedMap map) {

        String name = map.getString("Name");
        Material material = map.getMaterial("Material").toMaterial();
        HashMap<ItemAttribute, Integer> attributes = map.getMap("Attributes", ItemAttribute.class, Integer.class);
        Tier currentTier = map.get("Tier", Tier.class);
        UUID uuid = map.getUUID("UUID");


        return new EnchantableItem(name,material,attributes,currentTier, uuid);
    }
}