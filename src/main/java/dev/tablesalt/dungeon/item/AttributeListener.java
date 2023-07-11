package dev.tablesalt.dungeon.item;

import dev.tablesalt.gamelib.players.PlayerCache;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;

public class AttributeListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    private void onClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!isInGame(player))
            return;

        ItemStack clickedItem = event.getItem();
        ItemAttribute attribute = getAttributeFor(clickedItem);

        if (attribute == null)
            return;

       attribute.getActions().onClick(player,clickedItem,event);
    }


    private boolean isInGame(Player player) {
        return PlayerCache.from(player).getGameIdentifier().hasGame();
    }

    private ItemAttribute getAttributeFor(ItemStack item) {

        for (ItemAttribute attribute : ItemAttribute.getRegisteredAttributes())
            if (attribute.hasAttribute(item))
                return attribute;


        return null;
    }







}
