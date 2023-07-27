package dev.tablesalt.dungeon.listener;

import dev.tablesalt.dungeon.DungeonSettings;
import dev.tablesalt.dungeon.DungeonStaticSettings;
import dev.tablesalt.dungeon.database.DungeonCache;
import dev.tablesalt.dungeon.util.MessageUtil;
import dev.tablesalt.dungeon.util.sound.TBSSound;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.menu.Menu;

public class InDungeonListener implements Listener {


    @EventHandler
    public void onChestLoot(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        DungeonCache cache = DungeonCache.from(player);
        Inventory inventory = event.getClickedInventory();

        if (Menu.getMenu(player) != null) return;

        if (inventory == null || !inventory.getType().equals(InventoryType.CHEST)) return;

        ItemStack stack = inventory.getItem(event.getSlot());

        if (stack == null)
            return;

        if (stack.getType().equals(Material.GOLD_NUGGET)) {
            int amount = stack.getAmount();

            cache.giveMoney(amount * DungeonStaticSettings.Loot.moneyPerNugget);
            TBSSound.MoneyPickup.getInstance().playTo(player);

            inventory.setItem(event.getSlot(),null);
        }
    }
}
