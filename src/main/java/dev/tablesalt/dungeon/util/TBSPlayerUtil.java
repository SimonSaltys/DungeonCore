package dev.tablesalt.dungeon.util;

import dev.tablesalt.dungeon.database.DungeonCache;
import dev.tablesalt.dungeon.game.DungeonGame;
import dev.tablesalt.dungeon.maps.DungeonMap;
import dev.tablesalt.gamelib.players.PlayerCache;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.remain.Remain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public class TBSPlayerUtil {

    public DungeonMap getMapSafe(Player player) {
        PlayerCache cache = PlayerCache.from(player);
        DungeonGame game = (DungeonGame) cache.getGameIdentifier().getCurrentGame();

        Valid.checkNotNull(game, "Player is not in a game!");
        DungeonMap map = game.getMapRotator().getCurrentMap();

        Valid.checkNotNull(map, "Player is not in a map!");
        return map;
    }

    public void giveItem(Player player, ItemStack itemStack) {
        if (itemStack == null)
            return;

        HashMap<Integer, ItemStack> failedItems = player.getInventory().addItem(itemStack);

        if (!failedItems.containsValue(itemStack))
            if (TBSItemUtil.isEnchantable(itemStack))
                DungeonCache.from(player).addEnchantableItem(itemStack);

        for (Map.Entry<Integer, ItemStack> entry : failedItems.entrySet()) {
            player.getWorld().dropItemNaturally(player.getLocation(), entry.getValue());
        }
    }

    public ItemStack[] getAllItemsExcludingArmor(Player player) {
        PlayerInventory inventory = player.getInventory();
        ItemStack[] items = new ItemStack[46];

        for (int i = 0; i < 36; i++) {
            items[i] = inventory.getItem(i);
        }

        return items;
    }

    public List<Player> getPlayersNotInGame() {
        List<Player> players = new ArrayList<>();

        for (Player player : Remain.getOnlinePlayers())
            if (!PlayerCache.from(player).getGameIdentifier().hasGame())
                players.add(player);

        return players;

    }

    // ------–------–------–------–------–------–------–------–------–------–------–------–----
    // Player Location Methods
    // ------–------–------–------–------–------–------–------–------–------–------–------–----


    public boolean isOnGround(Player player) {
        return player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR;
    }

    public void launchPlayerWhereLooking(LivingEntity player, double horizontalPush, double verticalPush, Location originLocation) {
        Vector playerHorizontalDir = player.getVelocity();
        playerHorizontalDir.setY(0).normalize();
        playerHorizontalDir.multiply(horizontalPush);

        Vector playerLookDir = originLocation.getDirection();
        playerLookDir.setY(0).normalize();

        player.setVelocity(player.getVelocity().add(new Vector(playerLookDir.getX(), verticalPush, playerLookDir.getZ())));
    }

    public void launchPlayerAwayFrom(LivingEntity player, double verticalPush, Location originLocation) {
        Vector victimVector = player.getLocation().toVector().setY(0);
        Vector originVector = originLocation.toVector().setY(0);
        Vector horizontalDir = victimVector.subtract(originVector);
        horizontalDir.normalize();

        player.setVelocity(new Vector(horizontalDir.getX(), verticalPush, horizontalDir.getZ()));
    }

    // ------–------–------–------–------–------–------–------–------–------–------–------–----
    // Getting entites around player
    // ------–------–------–------–------–------–------–------–------–------–------–------–----

    /**
     * Checks if the player is behind another player
     *
     * @param originPlayer
     * @param checkPlayer
     * @return
     */
    public boolean isBehind(Player originPlayer, Player checkPlayer) {
        Location originLocation = originPlayer.getEyeLocation();
        Location checkLocation = checkPlayer.getEyeLocation();

        Vector checkVector = originLocation.toVector().subtract(checkLocation.toVector());
        Vector originPlayerLookDir = originPlayer.getEyeLocation().getDirection();

        double delta = checkVector.dot(originPlayerLookDir);
        return delta > 0;

    }
}
