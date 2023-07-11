package dev.tablesalt.dungeon.item;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.remain.CompMetadata;

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

    /**
     * The nbt tag that is applied to the item
     * <p>
     * Must be unique and set yourself
     */
    private final String tag;

    /**
     * Where the listener method overrides are
     * located for you to provide functionality
     */
    private final AttributeActions actions;

    public ItemAttribute(String tag) {
        this.tag = tag;
        this.actions = getActions();
        registeredAttributes.add(this);
    }

    protected abstract AttributeActions getActions();

    public final ItemStack equipTagTo(ItemStack item) {
        return CompMetadata.setMetadata(item,tag,"true");
        //todo save to database that this item has this specific tag for this player

    }

    public final ItemStack dequipTagFrom(ItemStack item) {
        return CompMetadata.setMetadata(item,tag,"false");
        //todo save to database that this item no longer has this specific tag for this player

    }

    public List<ItemStack> getItemsWithAttribute(Player player) {
        List<ItemStack> itemsWithAttribute = new ArrayList<>();

        for (ItemStack itemStack : player.getInventory().getContents())
            if (hasAttribute(itemStack))
                itemsWithAttribute.add(itemStack);

        return itemsWithAttribute;
    }

    public final boolean hasAttribute(ItemStack item) {
       String value = CompMetadata.getMetadata(item,tag);

       return Boolean.parseBoolean(value);
    }

    public final String getTag() {
        return tag;
    }


    public static List<ItemAttribute> getRegisteredAttributes() {
       return Collections.unmodifiableList(registeredAttributes);
    }

}
