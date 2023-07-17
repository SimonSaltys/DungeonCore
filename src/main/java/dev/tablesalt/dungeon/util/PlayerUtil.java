package dev.tablesalt.dungeon.util;

import dev.tablesalt.dungeon.game.DungeonGame;
import dev.tablesalt.dungeon.maps.DungeonMap;

import dev.tablesalt.gamelib.players.PlayerCache;

import lombok.experimental.UtilityClass;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.Valid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public class PlayerUtil {

    public DungeonMap getMapSafe(Player player) {
        PlayerCache cache = PlayerCache.from(player);
        DungeonGame game = (DungeonGame) cache.getGameIdentifier().getCurrentGame();

        Valid.checkNotNull(game, "Player is not in a game!");
        DungeonMap map = game.getMapRotator().getCurrentMap();

        Valid.checkNotNull(map, "Player is not in a map!");
        return map;
    }

    public void giveItem(Player player,ItemStack itemStack) {
        if (itemStack == null)
            return;

        HashMap<Integer, ItemStack> failedItems = player.getInventory().addItem(itemStack);

        for(Map.Entry<Integer, ItemStack> entry : failedItems.entrySet()) {
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



}
