package dev.tablesalt.dungeon.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.tablesalt.dungeon.item.ItemAttribute;
import dev.tablesalt.dungeon.item.Rarity;
import dev.tablesalt.dungeon.item.Tier;
import dev.tablesalt.dungeon.util.TBSItemUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.core.util.JsonUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.ItemUtil;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.model.ConfigSerializable;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.CompMetadata;

import java.util.*;
import java.util.function.BiConsumer;

@Getter
@Setter
public class EnchantableItem implements ConfigSerializable {

    public static final Integer MAX_ENCHANTS_PER_ITEM = 3;

    private String name;

    private Material material;

    private Map<ItemAttribute, Integer> attributeTierMap;

    private ItemAttribute lastAdded;


    private Tier currentTier;

    private final UUID uuid;

    public EnchantableItem(UUID uuid) {
        this(uuid, "", Material.AIR, new HashMap<>(), Tier.NONE);
    }


    public EnchantableItem(UUID uuid, String name, Material material, Map<ItemAttribute, Integer> attributeTierMap, Tier tier) {
        this.name = name;
        this.material = material;
        this.attributeTierMap = attributeTierMap;
        this.currentTier = tier;


        this.uuid = uuid;
    }

    public static EnchantableItem makeArmor() {
        return new EnchantableItem(UUID.randomUUID(), "Tunic", Material.LEATHER_CHESTPLATE, new HashMap<>(), Tier.NONE);
    }

    public static EnchantableItem makeWeapon() {
        return new EnchantableItem(UUID.randomUUID(), "Sword", Material.GOLDEN_SWORD, new HashMap<>(), Tier.NONE);

    }

    @Override
    public SerializedMap serialize() {
        return SerializedMap.ofArray(
                "Name", name,
                "Material", material,
                "Attributes", serializeAttributeTierMap(),
                "Tier", currentTier,
                "UUID", uuid
        );
    }

    private Map<String, Integer> serializeAttributeTierMap() {
        Map<String, Integer> serializedMap = new HashMap<>();

        // Transform ItemAttribute keys into strings and populate the new map
        for (Map.Entry<ItemAttribute, Integer> entry : attributeTierMap.entrySet()) {
            serializedMap.put(entry.getKey().getName(), entry.getValue());
        }

        return serializedMap;
    }

    public static Map<ItemAttribute,Integer> deserializeAttributeTierMap(String jsonMap) {
        Map<ItemAttribute,Integer> finalMap = new HashMap<>();

        try {
            Map<String,Integer> result = new ObjectMapper().readValue(jsonMap, new TypeReference<>(){});

            for (String nameOfAttribute : result.keySet()) {
                ItemAttribute attribute = ItemAttribute.fromName(nameOfAttribute);

                if (attribute == null)
                    continue;

                finalMap.put(attribute,result.get(nameOfAttribute));
            }

        } catch (JsonProcessingException ex) {
            Common.error(ex,"Could not deserialize jsonMap");
        }


        return finalMap;
    }

    @Override
    public String toString() {
        return "[" + name + ", "
                + material + ", "
                + Common.convert(attributeTierMap.keySet(), ItemAttribute::getName) + ", "
                + currentTier + "]";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof EnchantableItem && ((EnchantableItem) obj).getUuid().equals(uuid);
    }


    /**
     * Attempts to add the attribute and current tier
     * if not already added
     */
    public void addAttribute(ItemAttribute attribute, Tier tier) {
        if (attributeTierMap.containsKey(attribute))
            return;

        attributeTierMap.put(attribute, tier.getAsInteger());
        lastAdded = attribute;
    }


    /**
     * Compiles the data from this object
     * into an itemstack that can be used by the player
     */
    public ItemStack compileToItemStack() {
        String formattedName = getFormattedName();

        ItemStack compiledItem = ItemCreator.of(CompMaterial.fromMaterial(material), formattedName).lore(getLores()).unbreakable(true).hideTags(true).make();


        return setNBTOnItem(compiledItem);
    }

    public List<String> getLores() {
        List<String> lore = new ArrayList<>();

        for (ItemAttribute attribute : attributeTierMap.keySet())
            lore.addAll(attribute.getAttributeLore(Tier.fromInteger(attributeTierMap.get(attribute))));

        return lore;
    }

    public Tier getTierFor(ItemAttribute attribute) {

        if (attributeTierMap.containsKey(attribute))
            return Tier.fromInteger(attributeTierMap.get(attribute));

        return Tier.NONE;
    }

    public String getFormattedName() {
        return currentTier.getColor().getChatColor() + Rarity.MYTHIC.toString() + " " + ItemUtil.bountifyCapitalized(name) + " " +
                (currentTier != Tier.NONE ? "&l" + currentTier.getAsRomanNumeral() + " " : "");
    }

    public void forAllAttributes(BiConsumer<ItemAttribute, Tier> consumer) {
        for (ItemAttribute attribute : attributeTierMap.keySet())
            consumer.accept(attribute, Tier.fromInteger(attributeTierMap.get(attribute)));

    }

    private ItemStack setNBTOnItem(ItemStack item) {

        item = CompMetadata.setMetadata(item, "UUID", uuid.toString());


        item = CompMetadata.setMetadata(item, "Tier", currentTier.getAsInteger() + "");

        int attributeAdded = 0;
        for (ItemAttribute attribute : attributeTierMap.keySet()) {
            attributeAdded++;
            item = CompMetadata.setMetadata(item, "attribute_" + attributeAdded,
                    SerializedMap.ofArray(
                            "Name", attribute.getName(),
                            "Tier", attributeTierMap.get(attribute)).toJson());
        }
        return item;
    }


    /*----------------------------------------------------------------*/
    /* STATIC ACCESS */
    /*----------------------------------------------------------------*/


    /**
     * Deserializes the itemstacks nbt data and returns it
     * as an enchantable item representation.
     */
    public static EnchantableItem fromItemStack(ItemStack item) {
        if (item == null)
            return null;

        String uuidString = CompMetadata.getMetadata(item, "UUID");
        String tierString = CompMetadata.getMetadata(item, "Tier");

        if (uuidString == null)
            return null;

        EnchantableItem enchantableItem = new EnchantableItem(UUID.fromString(uuidString));

        if (tierString != null) {
            int integer = Integer.parseInt(tierString);
            enchantableItem.setCurrentTier(Tier.fromInteger(integer));
        }

        enchantableItem.setMaterial(item.getType());
        enchantableItem.setName(item.getType().equals(Material.LEATHER_CHESTPLATE) ? "Tunic" : item.getType().toString());


        for (int i = 0; i < MAX_ENCHANTS_PER_ITEM; i++) {
            String json = CompMetadata.getMetadata(item, "attribute_" + (i + 1));
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
        Tier currentTier = map.get("Tier", Tier.class);
        UUID uuid = map.getUUID("UUID");
        Map<ItemAttribute, Integer> attributes = nameToItemMap(map.getMap("Attributes", String.class, Integer.class));

        return new EnchantableItem(uuid, name, material, attributes, currentTier);
    }

    public static Map<ItemAttribute, Integer> nameToItemMap(HashMap<String, Integer> attributes) {

        return Common.convert(attributes, new Common.MapToMapConverter<>() {
            @Override
            public ItemAttribute convertKey(String key) {
                return ItemAttribute.fromName(key);
            }

            @Override
            public Integer convertValue(Integer value) {
                return value;
            }
        });
    }


    public static HashSet<EnchantableItem> getArmorAndWeapon(Player player) {
        HashSet<EnchantableItem> enchantableItems = new HashSet<>();

        for (ItemStack stack : player.getInventory().getArmorContents()) {
            EnchantableItem item = EnchantableItem.fromItemStack(stack);

            if (item != null) {
                enchantableItems.add(item);
                break;
            }
        }

        EnchantableItem item = EnchantableItem.fromItemStack(player.getInventory().getItemInMainHand());

        if (item != null)
            enchantableItems.add(item);

        return enchantableItems;

    }
}
