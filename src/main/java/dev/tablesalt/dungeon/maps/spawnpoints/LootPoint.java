package dev.tablesalt.dungeon.maps.spawnpoints;


import dev.tablesalt.dungeon.DungeonStaticSettings;
import dev.tablesalt.dungeon.database.EnchantableItem;
import dev.tablesalt.dungeon.maps.DungeonMap;
import dev.tablesalt.dungeon.configitems.LootChance;
import dev.tablesalt.dungeon.util.TBSItemUtil;
import dev.tablesalt.gamelib.game.map.GameMap;
import lombok.Getter;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.ChatUtil;
import org.mineacademy.fo.RandomUtil;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompChatColor;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.ArrayList;
import java.util.List;

/**
 * Hold the data to spawn a chest with
 * randomly provided loot through the LootChance class
 */
@Getter
public class LootPoint extends SpawnPoint {

    private final Location location;

    private LootChance lootChance;

    public enum DropType {
        MYTHIC,
        GOLD,
        NO_ITEM,
        NORMAL_ITEM;
    }

    public LootPoint(Location location) {
        this.location = location;

    }

     public void setLootChance(LootChance lootChance) {
         this.lootChance = lootChance;
         saveMap();
     }

     @Override
    public void spawn() {
        Block block = location.clone().add(0,1,0).getBlock();
        block.setType(Material.CHEST);

       Chest chest = (Chest) block.getState();
       Inventory inventory = chest.getInventory();

       assignLootRandomly(inventory);
    }

    private void assignLootRandomly(Inventory inventory) {
        if (lootChance == null)
            lootChance = RandomUtil.nextItem(LootChance.getChances());

        final List<Pair<DropType, Double>> itemWeights = new ArrayList<>();

//        itemWeights.add(new Pair<>(LootPoint.DropType.NO_ITEM, 0.9));
//        itemWeights.add(new Pair<>(LootPoint.DropType.MYTHIC, 0.01));
//        itemWeights.add(new Pair<>(LootPoint.DropType.GOLD, 0.09));

        itemWeights.add(new Pair<>(LootPoint.DropType.NO_ITEM, 0.9));
        itemWeights.add(new Pair<>(LootPoint.DropType.MYTHIC, lootChance.getMythicDropChance()));
        itemWeights.add(new Pair<>(LootPoint.DropType.GOLD, lootChance.getGoldDropChance()));

        EnumeratedDistribution<DropType> distribution = new EnumeratedDistribution<>(itemWeights);

        int mysticsAdded = 0;
        int goldAdded = 0;

        for (int itemsAdded = 0; itemsAdded < inventory.getSize(); itemsAdded++) {

            if (mysticsAdded + goldAdded >= lootChance.getMaxTotalDrops())
                return;

            DropType dropType = distribution.sample();

            ItemStack itemToAdd = null;

            if (dropType == DropType.MYTHIC && mysticsAdded < lootChance.getMaxMythicDrops()) {
                itemToAdd = EnchantableItem.makeArmor().compileToItemStack();
                mysticsAdded++;

            } else if(dropType == DropType.GOLD && goldAdded < lootChance.getMaxGoldDrops()) {
                int amount = RandomUtils.nextInt(1, 10);
                itemToAdd = ItemCreator.of(CompMaterial.GOLD_NUGGET,
                        ChatUtil.generateGradient("Gold Nugget", CompChatColor.YELLOW,CompChatColor.GOLD))
                        .amount(amount).lore("","&7Pickup to gain &6" + amount * DungeonStaticSettings.Loot.MONEY_PER_NUGGET + "g").make();
                goldAdded++;

            } if (itemToAdd == null)
                itemToAdd = ItemCreator.of(CompMaterial.AIR).make();

            inventory.setItem(itemsAdded,itemToAdd);
        }


    }

    @Override
    public Location getLocation() {
        return location;
    }

    public void saveMap() {
       GameMap map = GameMap.findMapFromLocation(location);

       if (map instanceof DungeonMap dungeonMap)
            dungeonMap.save();
    }

    @Override
    public SerializedMap serialize() {
        return SerializedMap.ofArray(
                    "location", location,
                    "loot_chance", lootChance);
    }


     public static LootPoint deserialize(SerializedMap map) {
         LootPoint point = new LootPoint(map.getLocation("location"));
         point.setLootChance(map.get("loot_chance", LootChance.class));

         return point;
    }
}
