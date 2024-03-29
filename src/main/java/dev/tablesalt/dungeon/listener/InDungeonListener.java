package dev.tablesalt.dungeon.listener;

import dev.tablesalt.dungeon.DungeonStaticSettings;
import dev.tablesalt.dungeon.database.DungeonCache;
import dev.tablesalt.dungeon.database.EnchantableItem;
import dev.tablesalt.dungeon.database.Keys;
import dev.tablesalt.dungeon.model.DungeonLeaveReason;
import dev.tablesalt.dungeon.model.TBSSound;
import dev.tablesalt.dungeon.nms.PlayerCorpse;
import dev.tablesalt.dungeon.util.EntityUtil;
import dev.tablesalt.dungeon.util.TBSPlayerUtil;
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
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.PlayerUtil;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.remain.CompMetadata;

import java.util.HashSet;
import java.util.Iterator;

public class InDungeonListener implements Listener {

    public InDungeonListener() {
        Common.runTimer(1, new BoostTask());
    }

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

            cache.giveMoney(amount * DungeonStaticSettings.Loot.MONEY_PER_NUGGET);
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

            if (display == null || !CompMetadata.hasMetadata(display, Keys.DEAD_BODY_NAME))
                return;


            PlayerCorpse corpseToLoot = PlayerCorpse.getFromPlayerName(CompMetadata.getMetadata(display, Keys.DEAD_BODY_NAME));

            if (corpseToLoot == null)
                return;

            corpseToLoot.displayLootTo(player);
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

    private final HashSet<Player> boostedPlayers = new HashSet<>();

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();

        if (!checkInGame(player)) {
            return;
        }

        if (PlayerCache.from(player).getTagger().getBooleanTagSafe(Keys.ALLOW_FLIGHT)) {
            event.setCancelled(true);

            player.setAllowFlight(false);
            player.setFlying(false);

            if (!boostedPlayers.contains(player)) {
                for (EnchantableItem item : EnchantableItem.getArmorAndWeapon(player))
                    item.forAllAttributes((itemAttribute, tier) -> itemAttribute.onToggleFlight(player, item, tier, event));
                boostedPlayers.add(player);
            }
        } else
            event.setCancelled(true);
    }


    private class BoostTask extends BukkitRunnable {
        @Override
        public void run() {

            Iterator<Player> itr = boostedPlayers.iterator();

            while (itr.hasNext()) {
                Player player = itr.next();
                boolean isFalling = player.getVelocity().getY() < 0.1;

                if (TBSPlayerUtil.isOnGround(player)) {
                    player.setAllowFlight(true);

                    itr.remove();
                } else if (isFalling)
                    player.setAllowFlight(false);
            }


        }
    }
}
