package dev.tablesalt.dungeon.item;

import dev.tablesalt.dungeon.DungeonPlugin;
import dev.tablesalt.dungeon.item.impl.Rarity;
import dev.tablesalt.dungeon.item.impl.Tier;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.ReflectionUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A way to apply custom listeners to an Item a player is using
 */
public abstract class ItemAttribute {

   /**
    * Holds all registered item attributes
    * <p>
    * Attributes are registered automatically
    */
   private static final List<ItemAttribute> registeredAttributes = new ArrayList<>();

   public ItemAttribute() {
   }

   public abstract String getName();

   public abstract Rarity getRarity();

  public abstract List<String> getAttributeLore(Tier tier);


   public static List<ItemAttribute> getRegisteredAttributes() {
      return Collections.unmodifiableList(registeredAttributes);
   }

   public static List<ItemAttribute> getAttributesOfRarity(Rarity rarity) {
       List<ItemAttribute> attributeList = new ArrayList<>();


       for (ItemAttribute attribute : registeredAttributes)
           if (attribute.getRarity().equals(rarity))
               attributeList.add(attribute);

       return attributeList;
   }

   public static ItemAttribute fromName(String name) {
       for (ItemAttribute attribute : getRegisteredAttributes())
           if (attribute.getName().equals(name))
               return attribute;
       return null;
   }

    public static void registerAttributes() {
       registeredAttributes.clear();
        // Auto-register all sub commands
        for (final Class<? extends ItemAttribute> clazz : ReflectionUtil.getClasses(DungeonPlugin.getInstance(), ItemAttribute.class)) {
            ItemAttribute kit = ReflectionUtil.instantiate(clazz);
            registeredAttributes.add(kit);
        }
    }

}
