package dev.tablesalt.dungeon.listener;

import dev.tablesalt.dungeon.DungeonStaticSettings;
import dev.tablesalt.dungeon.database.DungeonCache;
import dev.tablesalt.dungeon.database.Keys;
import dev.tablesalt.dungeon.util.DungeonLeaveReason;
import dev.tablesalt.dungeon.util.EntityUtil;
import dev.tablesalt.dungeon.util.sound.TBSSound;
import dev.tablesalt.gamelib.event.PlayerLeaveGameEvent;
import dev.tablesalt.gamelib.players.PlayerCache;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.PlayerUtil;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.remain.CompMetadata;

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

            inventory.setItem(event.getSlot(), null);
        }
    }

    @EventHandler
    public void onDeadBodyClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!checkInGame(player))
            return;

        //make this event only fire on right click as opposed to both right and left
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getHand() != EquipmentSlot.OFF_HAND) {
            //make sure they are clicking a block
            if (event.getClickedBlock() == null || event.getClickedBlock().getType() == Material.AIR)
                return;

            Block block = event.getClickedBlock();
            TextDisplay display = EntityUtil.getClosestTextDisplay(block.getLocation(), 2);

            if (display == null || !CompMetadata.hasMetadata(display, Keys.DEAD_BODY_NAME)) {
            }

//            PlayerCorpse corpseToLoot = PlayerCorpse.getFromPlayerName(CompMetadata.getMetadata(display, Keys.DEAD_BODY_NAME));

//            if (corpseToLoot == null)
//                return;

//            corpseToLoot.displayLootTo(player);
        }

    }

    @EventHandler
    public void onGameLeave(PlayerLeaveGameEvent event) {
        if (!event.getLeaveMessage().equals(DungeonLeaveReason.EXTRACTED))
            PlayerUtil.normalize(event.getPlayer(), true);
    }

    protected final boolean checkInGame(Player player) {
        PlayerCache cache = PlayerCache.from(player);
        return cache.getGameIdentifier().hasGame();
    }
}
