package dev.tablesalt.dungeon.item;

import dev.tablesalt.dungeon.DungeonPlugin;
import dev.tablesalt.dungeon.database.EnchantableItem;
import dev.tablesalt.dungeon.event.PlayerGainGoldEvent;
import dev.tablesalt.dungeon.util.TBSItemUtil;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.ReflectionUtil;

import java.util.*;

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


    /*----------------------------------------------------------------*/
    /* OVERRIDABLE LOGIC */
    /*----------------------------------------------------------------*/

    public void onPvP(Player attacker, Player victim, Tier tier, EntityDamageByEntityEvent event) {

    }

    public void onPvE(Player attacker, LivingEntity victim, Tier tier, EntityDamageByEntityEvent event) {

    }

    public void onClick(Player clicker, Tier tier, PlayerInteractEvent event) {

    }

    public void onDamaged(Player victim, Tier tier, EntityDamageEvent event) {

    }

    public void onGoldGain(Player player, double amountGained, Tier tier, PlayerGainGoldEvent event) {
    }

    public boolean isForArmor() {
        return false;
    }

    /*----------------------------------------------------------------*/
    /* STATIC UTILS */
    /*----------------------------------------------------------------*/


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

    public static Map<ItemAttribute, Integer> getAttributesOnMainHandItem(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();


        if (TBSItemUtil.isEnchantable(item)) {
            EnchantableItem enchantedItem = EnchantableItem.fromItemStack(item);
            if (enchantedItem != null)
                return enchantedItem.getAttributeTierMap();
        }

        return new HashMap<>();
    }


}
