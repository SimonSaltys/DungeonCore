package dev.tablesalt.dungeon.menu;

import dev.tablesalt.dungeon.util.TBSItemUtil;
import dev.tablesalt.dungeon.util.sound.TBSSound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.ItemUtil;
import org.mineacademy.fo.menu.Menu;

public class MenuListener implements Listener {


    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player))
            return;

        final Player player = (Player) event.getWhoClicked();
        final TBSMenu menu = (TBSMenu) Menu.getMenu(player);

        if (menu != null && event.getView().getType() == InventoryType.CHEST) {
            final int size = event.getView().getTopInventory().getSize();

            for (final int slot : event.getRawSlots()) {
                if (slot > size)
                    continue;

                final ItemStack cursor = Common.getOrDefault(event.getCursor(), event.getOldCursor());

                if (!event.isCancelled()) {
                    menu.onMenuDrag(player,slot,event.getType(),cursor);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player))
            return;

        final Player player = (Player) event.getWhoClicked();

        if (Menu.getMenu(player) instanceof TBSMenu tbsMenu) {


            Inventory inventory = event.getClickedInventory();
            if (tbsMenu != null && inventory != null && inventory.equals(player.getInventory()))
                if (TBSItemUtil.isEnchantable(inventory.getItem(event.getSlot())))
                    TBSSound.MenuPlace.getInstance().playTo(player);
        }




    }
}

