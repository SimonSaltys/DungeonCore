package dev.tablesalt.dungeon.item;

import dev.tablesalt.dungeon.item.impl.Tier;

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
      registeredAttributes.add(this);
   }

   public abstract String getName();

  public abstract List<String> getAttributeLore(Tier tier);


   public static List<ItemAttribute> getRegisteredAttributes() {
      return Collections.unmodifiableList(registeredAttributes);
   }

   public static ItemAttribute fromName(String name) {
       for (ItemAttribute attribute : getRegisteredAttributes())
           if (attribute.getName().equals(name))
               return attribute;
       return null;
   }

}
