package dev.tablesalt.dungeon.maps.spawnpoints;


import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.collection.SerializedMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class LootPoint extends SpawnPoint {

    private final Location location;
    @Getter
    private List<ItemStack> loot;

    public LootPoint(Location location) {
        this.location = location;
        this.loot = new ArrayList<>();
    }


    @Override
    public void spawn() {
        Stack<ItemStack> lootStack = new Stack<>();
        lootStack.addAll(loot);
        Collections.shuffle(lootStack);

        Block block = location.clone().add(0,1,0).getBlock();
        block.setType(Material.CHEST);

       Chest chest = (Chest) block.getState();
       assignLootRandomly(chest.getInventory(), lootStack);
    }

    private void assignLootRandomly(Inventory inventory, Stack<ItemStack> lootStack) {
        for (int i = 0; i < inventory.getSize(); i++) {

            if (lootStack.isEmpty())
                return;

            ItemStack item = lootStack.pop();

            if (item == null || item.getType().isAir())
                inventory.setItem(i, new ItemStack(Material.AIR));
            else
                inventory.setItem(i, item);
        }
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public SerializedMap serialize() {
        return SerializedMap.ofArray(
                    "location", location,
                    "loot", loot
            );
    }

    public void setLoot(List<ItemStack> lootToSet) {
       this.loot = lootToSet;

    }

    public static LootPoint deserialize(SerializedMap map) {
        LootPoint spawnPoint = new LootPoint(map.getLocation("location"));
        spawnPoint.loot = map.getList("loot", ItemStack.class);

        return spawnPoint;
    }
}
