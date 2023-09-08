package dev.tablesalt.dungeon.item;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import dev.tablesalt.dungeon.DungeonPlugin;
import dev.tablesalt.dungeon.database.EnchantableItem;
import dev.tablesalt.dungeon.event.PlayerGainGoldEvent;
import dev.tablesalt.gamelib.event.PlayerJoinGameEvent;
import dev.tablesalt.gamelib.event.PlayerLeaveGameEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.ReflectionUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This abstract class serves as a foundation for implementing custom attributes
 * that can be applied to items used by players.
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

    /**
     * Called when a player hits another player
     */
    public void onPvP(Player attacker, Player victim, EnchantableItem attackersItem, Tier tier, EntityDamageByEntityEvent event) {
    }

    /**
     * Called when a player hits a non player entity
     */
    public void onPvE(Player attacker, LivingEntity victim, EnchantableItem attackersItem, Tier tier, EntityDamageByEntityEvent event) {
    }

    /**
     * Called when this player clicks while the item is in their main hand
     */
    public void onClick(Player clicker, EnchantableItem item, Tier tier, PlayerInteractEvent event) {
    }

    /**
     * Called when the player takes any damage other than pvp
     */
    public void onDamaged(Player victim, EnchantableItem item, Tier tier, EntityDamageEvent event) {
    }

    public void onDamaged(Player victim, EnchantableItem item, Tier tier, EntityDamageByEntityEvent event) {
    }


    public void onGoldGain(Player player, EnchantableItem item, double amountGained, Tier tier, PlayerGainGoldEvent event) {
    }


    public void onArmorEquip(Player player, EnchantableItem item, Tier tier, PlayerArmorChangeEvent event) {
    }

    public void onArmorTakeOff(Player player, EnchantableItem item, Tier tier, PlayerArmorChangeEvent event) {
    }

    /**
     * This will be called for all custom items
     * in a players inventory.
     */
    public void onPlayerJoinGame(Player player, EnchantableItem item, Tier tier, PlayerJoinGameEvent event) {
    }

    /**
     * This will be called for all custom items
     * in a players inventory.
     */
    public void onPlayerLeaveGame(Player player, EnchantableItem item, Tier tier, PlayerLeaveGameEvent event) {
    }

    public void onDrop(Player player, EnchantableItem item, Tier tier, PlayerDropItemEvent event) {
    }

    public void onPickup(Player player, EnchantableItem item, Tier tier, EntityPickupItemEvent event) {
    }

    public void onToggleFlight(Player player, EnchantableItem item, Tier tier, PlayerToggleFlightEvent event) {

    }

    public ItemType getType() {
        return ItemType.NONE;
    }

    @Override
    public String toString() {
        return getName();
    }



    /*----------------------------------------------------------------*/
    /* STATIC UTILS */
    /*----------------------------------------------------------------*/

    /**
     * Retrieves a list of all registered attributes.
     *
     * @return A list of registered attributes.
     */
    public static List<ItemAttribute> getRegisteredAttributes() {
        return Collections.unmodifiableList(registeredAttributes);
    }

    /**
     * Retrieves a list of attributes of a specific rarity.
     *
     * @param rarity The rarity for which attributes are being retrieved.
     * @return A list of attributes with the specified rarity.
     */
    public static List<ItemAttribute> getAttributesOfRarity(Rarity rarity) {
        List<ItemAttribute> attributeList = new ArrayList<>();


        for (ItemAttribute attribute : registeredAttributes)
            if (attribute.getRarity().equals(rarity))
                attributeList.add(attribute);

        return attributeList;
    }

    /**
     * Retrieves an attribute instance based on its name.
     *
     * @param name The name of the attribute.
     * @return An instance of the attribute with the specified name.
     */
    public static ItemAttribute fromName(String name) {
        for (ItemAttribute attribute : getRegisteredAttributes())
            if (Common.stripColors(attribute.getName()).equalsIgnoreCase(Common.stripColors(name)))
                return attribute;
        return null;
    }

    /**
     * Registers attributes and adds them to the list of registered attributes.
     */
    public static void registerAttributes() {
        registeredAttributes.clear();
        // Auto-register all sub commands
        for (final Class<? extends ItemAttribute> clazz : ReflectionUtil.getClasses(DungeonPlugin.getInstance(), ItemAttribute.class)) {
            ItemAttribute kit = ReflectionUtil.instantiate(clazz);
            registeredAttributes.add(kit);
        }
    }


    /**
     * Calls the 'start' method of the EnchantmentLifecycle interface if applicable.
     */
    public static void callStart(Player player, EnchantableItem item, Tier tier, ItemAttribute attribute) {
        if (attribute instanceof EnchantmentLifecycle start)
            start.start(player, item, tier);
    }

    /**
     * Calls the 'stop' method of the EnchantmentLifecycle interface if applicable.
     */
    public static void callStop(Player player, EnchantableItem item, Tier tier, ItemAttribute attribute) {
        if (attribute instanceof EnchantmentLifecycle stoppable)
            stoppable.stop(player, item, tier);
    }


}
