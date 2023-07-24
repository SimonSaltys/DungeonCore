package dev.tablesalt.dungeon;


import lombok.Getter;
import org.bukkit.Location;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.FileUtil;
import org.mineacademy.fo.settings.YamlConfig;

import java.io.File;

public class DungeonSettings extends YamlConfig {

    @Getter
    private static final DungeonSettings instance = new DungeonSettings();

    @Getter
    private Location enchantingLocation;

    private DungeonSettings() {
        this.loadConfiguration(NO_DEFAULT, "BasicSettings.yml");


    }

    public void setEnchantingLocation(Location location) {
        enchantingLocation = location;
        save();
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        enchantingLocation = getLocation("Enchanting_Location",null);
    }

    @Override
    protected void onSave() {
        set("Enchanting_Location",enchantingLocation);

        super.onSave();
    }
}
