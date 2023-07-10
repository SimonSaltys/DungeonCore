package dev.tablesalt.dungeon.tools;


import dev.tablesalt.dungeon.maps.DungeonMap;
import dev.tablesalt.dungeon.game.DungeonGame;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.settings.FileConfig;

import java.util.List;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SpawnPointTool extends LocationListTool{

    @Getter
    private static final SpawnPointTool instance = new SpawnPointTool();

    @Override
    protected CompMaterial getBlockMask(Block block, Player player) {
        return CompMaterial.GLOWSTONE;
    }

    @Override
    protected String getBlockName(Block block, Player player) {
        return "[Player Spawn Point]";
    }

    @Override
    protected FileConfig.LocationList getLocationList(DungeonGame game) {
        return game.getMapRotator().getCurrentMap().getPlayerSpawnPoints();
    }

    @Override
    protected ItemStack getTool() {
        return ItemCreator.of(CompMaterial.PLAYER_HEAD, "&l&3SPAWN POINT TOOL").make();
    }

    @Override
    protected int getMaxPoints(DungeonMap map) {
        return map.getGame().getMaxPlayers();
    }

}
