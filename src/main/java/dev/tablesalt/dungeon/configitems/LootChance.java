package dev.tablesalt.dungeon.configitems;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.model.ConfigSerializable;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.settings.ConfigItems;
import org.mineacademy.fo.settings.YamlConfig;

import java.util.List;
import java.util.Objects;

public class LootChance extends YamlConfig implements ConfigSerializable {
    private static final ConfigItems<LootChance> loadedChances = ConfigItems.fromFolder("chances", LootChance.class);

    @Getter
    private double mythicDropChance;
    @Getter

    private int maxMythicDrops;
    @Getter

    private double goldDropChance;
    @Getter

    private int maxGoldDrops;
    @Getter

    private int maxTotalDrops;
    @Getter

    private final String name;

    private LootChance(String name) {
        this.name = name;

        setHeader(Common.configLine(),
                "Please use provided in game commands to edit these values.",
                Common.configLine());

        loadConfiguration(NO_DEFAULT, "chances/" + name + ".yml");
    }

    @Override
    protected void onLoad() {
        goldDropChance = getDouble("gold_drop_chance", 10.0);
        maxGoldDrops = getInteger("max_gold_drops", 8);
        mythicDropChance = getDouble("mystic_drop_chance", 5.0);
        maxMythicDrops = getInteger("max_mystic_drops", 2);
        maxTotalDrops = getInteger("max_total_drops", 10);

        this.save();
    }


    @Override
    protected void onSave() {
        super.onSave();
    }

    @Override
    public SerializedMap saveToMap() {
        return SerializedMap.ofArray(
                "gold_drop_chance", goldDropChance,
                "max_gold_drops", maxGoldDrops,
                "mystic_drop_chance", mythicDropChance,
                "max_mystic_drops", maxMythicDrops,
                "max_total_drops", maxTotalDrops);
    }

    @Override
    public SerializedMap serialize() {
        return SerializedMap.ofArray("Name", name);
    }

    public static LootChance deserialize(SerializedMap map) {
        return makeLootChance(map.getString("Name"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        LootChance chance = (LootChance) o;
        return Objects.equals(name, chance.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }

    public static void loadChances() {
        loadedChances.loadItems();
    }

    public static LootChance makeLootChance(String name) {
        return loadedChances.loadOrCreateItem(name, () -> new LootChance(name));
    }

    public static void removeChance(LootChance chance) {
        loadedChances.removeItem(chance);
    }

    public static LootChance getLootChance(String name) {
        return loadedChances.findItem(name);
    }

    public static boolean isChanceLoaded(String name) {
        return loadedChances.isItemLoaded(name);
    }

    public static List<LootChance> getChances() {
        return loadedChances.getItems();
    }


    public ItemStack convertToItem() {
        return ItemCreator.of(CompMaterial.BOOK, name,
                "&bMythic &7Drop Chance: &6" + mythicDropChance,
                "&bMax Mythic &7Drops: &6" + maxMythicDrops,
                " ",
                "&eGold Drop &7Chance: &6" + mythicDropChance,
                "&eMax Gold &7Drops: &6" + maxGoldDrops,
                " ",
                "&aMax Total &7Drops: &6" + maxTotalDrops).make();
    }


    public void setMythicDropChance(double mythicDropChance) {

        this.mythicDropChance = mythicDropChance;
        save();
    }

    public void setMaxMythicDrops(int maxMythicDrops) {
        this.maxMythicDrops = maxMythicDrops;
        save();
    }

    public void setGoldDropChance(double goldDropChance) {
        this.goldDropChance = goldDropChance;
        save();
    }

    public void setMaxGoldDrops(int maxGoldDrops) {
        this.maxGoldDrops = maxGoldDrops;
        save();
    }

    public void setMaxTotalDrops(int maxTotalDrops) {
        this.maxTotalDrops = maxTotalDrops;
        save();
    }


}

