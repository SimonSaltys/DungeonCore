package dev.tablesalt.dungeon.maps.spawnpoints;


import dev.tablesalt.gamelib.game.map.GameMap;
import dev.tablesalt.gamelib.game.utils.GameUtil;
import lombok.Getter;
import lombok.Setter;
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
@Setter @Getter
public class LootPoint extends SpawnPoint {

    private final Location location;


    protected double mysticDropChance = 5.0;

    protected int maxMysticDrops = 2;

    protected double goldDropChance = 50.0;

    protected int maxGoldDrops = 10;

    protected int maxTotalDrops = 5;

    public LootPoint(Location location) {
        this.location = location;

    }


    @Override
    public void spawn() {


        Block block = location.clone().add(0,1,0).getBlock();
        block.setType(Material.CHEST);

       Chest chest = (Chest) block.getState();
    }

    private void assignLootRandomly(Inventory inventory, Stack<ItemStack> lootStack) {

    }

    @Override
    public Location getLocation() {
        return location;
    }

    public void saveMap() {
        //todo find map based on location
    }

    @Override
    public SerializedMap serialize() {
        return SerializedMap.ofArray(
                    "location", location,
                    "Mystic_Drop_Chance", mysticDropChance,
                    "Max_Mystic_Drops", maxMysticDrops,
                    "Gold_Drop_Chance", goldDropChance,
                    "Max_Gold_Drops", maxGoldDrops

            );
    }



    public static LootPoint deserialize(SerializedMap map) {
        LootPoint lootPoint = new LootPoint(map.getLocation("location"));

        lootPoint.setMysticDropChance(map.getDouble("Mystic_Drop_Chance", 5.0));
        lootPoint.setMaxMysticDrops(map.getInteger("Max_Mystic_Drops", 0));
        lootPoint.setGoldDropChance(map.getDouble("Gold_Drop_Chance", 5.0));
        lootPoint.setMaxGoldDrops(map.getInteger("Max_Gold_Drops", 0));;

        return lootPoint;
    }
}
