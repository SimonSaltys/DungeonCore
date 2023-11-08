package dev.tablesalt.dungeon.listener;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import dev.tablesalt.dungeon.database.EnchantableItem;
import dev.tablesalt.dungeon.event.PlayerGainGoldEvent;
import dev.tablesalt.dungeon.item.EnchantmentLifecycle;
import dev.tablesalt.dungeon.item.ItemAttribute;
import dev.tablesalt.gamelib.event.PlayerJoinGameEvent;
import dev.tablesalt.gamelib.event.PlayerLeaveGameEvent;
import dev.tablesalt.gamelib.players.PlayerCache;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class AttributeListener implements Listener {

    @EventHandler
    public void onPlayerGainGold(PlayerGainGoldEvent event) {
        Player player = event.getPlayer();
        EnchantableItem item = EnchantableItem.fromItemStack(player.getInventory().getItemInMainHand());

        if (item == null)
            return;

        item.forAllAttributes(((itemAttribute, tier)
                -> itemAttribute.onGoldGain(player, item, event.getAmountGained(), tier, event)));
    }

    @EventHandler
    public void onArmorEquip(PlayerArmorChangeEvent event) {
        Player player = event.getPlayer();

        PlayerCache cache = PlayerCache.from(player);

        if (!cache.getGameIdentifier().hasGame())
            return;

        EnchantableItem armorToEquip = EnchantableItem.fromItemStack(event.getNewItem());
        EnchantableItem armorToRemove = EnchantableItem.fromItemStack(event.getOldItem());

        if (armorToEquip == null && armorToRemove != null)
            armorToRemove.forAllAttributes(((itemAttribute, tier)
                    -> {
                itemAttribute.onArmorTakeOff(player, armorToRemove, tier, event);
                ItemAttribute.callStop(player, armorToRemove, tier, itemAttribute);
            }));

        if (armorToEquip != null)
            armorToEquip.forAllAttributes(((itemAttribute, tier)
                    -> {
                itemAttribute.onArmorEquip(player, armorToEquip, tier, event);
                ItemAttribute.callStart(player, armorToEquip, tier, itemAttribute);
            }));
    }

    @EventHandler
    public void onGameJoin(PlayerJoinGameEvent event) {
        Player player = event.getPlayer();

        ItemStack[] allContents = player.getInventory().getContents();

        for (ItemStack item : allContents) {
            EnchantableItem enchantableItem = EnchantableItem.fromItemStack(item);

            if (enchantableItem == null)
                continue;

            enchantableItem.forAllAttributes((itemAttribute, tier)
                    -> {
                itemAttribute.onPlayerJoinGame(player, enchantableItem, tier, event);
                ItemAttribute.callStart(player, enchantableItem, tier, itemAttribute);
            });
        }
    }

    @EventHandler
    public void onGameLeave(PlayerLeaveGameEvent event) {
        Player player = event.getPlayer();

        ItemStack[] allContents = player.getInventory().getContents();

        for (ItemStack item : allContents) {
            EnchantableItem enchantableItem = EnchantableItem.fromItemStack(item);

            if (enchantableItem == null)
                continue;

            enchantableItem.forAllAttributes((itemAttribute, tier)
                    -> {
                itemAttribute.onPlayerLeaveGame(player, enchantableItem, tier, event);
                ItemAttribute.callStop(player, enchantableItem, tier, itemAttribute);
            });
        }

        //double checks that the player has no active attributes, if they do then this stops them
        for (ItemAttribute itemAttribute : ItemAttribute.getRegisteredAttributes())
            if (itemAttribute instanceof EnchantmentLifecycle lifecycle)
                lifecycle.stop(player, null, null);


    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        ItemStack dropped = event.getItemDrop().getItemStack();

        EnchantableItem enchantableItem = EnchantableItem.fromItemStack(dropped);

        if (enchantableItem == null)
            return;


        enchantableItem.forAllAttributes((itemAttribute, tier)
                -> {
            itemAttribute.onDrop(player, enchantableItem, tier, event);
            ItemAttribute.callStop(player, enchantableItem, tier, itemAttribute);
        });

    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player))
            return;

        ItemStack pickedUp = event.getItem().getItemStack();

        EnchantableItem enchantableItem = EnchantableItem.fromItemStack(pickedUp);

        if (enchantableItem == null)
            return;

        enchantableItem.forAllAttributes((itemAttribute, tier)
                -> {
            itemAttribute.onPickup(player, enchantableItem, tier, event);
            ItemAttribute.callStart(player, enchantableItem, tier, itemAttribute);
        });
    }

}
