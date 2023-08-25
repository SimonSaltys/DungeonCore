package dev.tablesalt.dungeon.item;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import dev.tablesalt.dungeon.DungeonPlugin;
import dev.tablesalt.dungeon.database.EnchantableItem;
import dev.tablesalt.dungeon.event.PlayerGainGoldEvent;
import dev.tablesalt.dungeon.util.TBSItemUtil;
import dev.tablesalt.gamelib.event.PlayerJoinGameEvent;
import dev.tablesalt.gamelib.event.PlayerLeaveGameEvent;
import io.papermc.paper.event.player.PlayerPickItemEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.ReflectionUtil;

import java.util.*;
import java.util.function.Consumer;

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

    //start the runnables and such.
    public void onGameJoin(Player player) {

    }


    /*----------------------------------------------------------------*/
    /* OVERRIDABLE LOGIC */
    /*----------------------------------------------------------------*/

    /**
     * Called when a player hits another player
     */
    public void onPvP(Player attacker, Player victim, Tier tier, EntityDamageByEntityEvent event) {

    }

    /**
     * Called when a player hits a non player entity
     */
    public void onPvE(Player attacker, LivingEntity victim, Tier tier, EntityDamageByEntityEvent event) {
    }

    /**
     * Called when this player clicks while the item is in their main hand
     */
    public void onClick(Player clicker, Tier tier, PlayerInteractEvent event) {
    }

    /**
     * Called when the player takes any damage other than pvp
     */
    public void onDamaged(Player victim, Tier tier, EntityDamageEvent event) {

    }


    public void onGoldGain(Player player, double amountGained, Tier tier, PlayerGainGoldEvent event) {
    }


    public void onArmorEquip(Player player, Tier tier, PlayerArmorChangeEvent event) {

    }

    public void onArmorTakeOff(Player player, Tier tier, PlayerArmorChangeEvent event) {

    }

    /**
     * This will be called for all custom items
     * in a players inventory.
     */
    public void onPlayerJoinGame(Player player, Tier tier, PlayerJoinGameEvent event) {
    }

    /**
     * This will be called for all custom items
     * in a players inventory.
     */
    public void onPlayerLeaveGame(Player player, Tier tier, PlayerLeaveGameEvent event) {
    }

    public void onDrop(Player player, Tier tier, PlayerDropItemEvent event) {

    }

    public void onPickup(Player player, Tier tier, PlayerPickItemEvent event) {

    }

    /**
     * if this is true then this attribute can only be applied
     * to armor. The inverse aswell is true
     * @return
     */
    public boolean isForArmor() {
        return false;
    }

    @Override
    public String toString() {
        return getName();
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
            if (Common.stripColors(attribute.getName()).equalsIgnoreCase(Common.stripColors(name)))
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
