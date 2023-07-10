package dev.tablesalt.dungeon.tools;

import dev.tablesalt.dungeon.game.DungeonGame;
import dev.tablesalt.dungeon.maps.DungeonMap;
import dev.tablesalt.dungeon.maps.spawnpoints.LootPoint;
import dev.tablesalt.dungeon.menu.LootSpawnMenu;
import dev.tablesalt.dungeon.util.PlayerUtil;
import dev.tablesalt.gamelib.tools.GameTool;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.Messenger;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.List;

@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class LootSpawnTool extends GameTool<DungeonGame> {

    @Getter
    private static final LootSpawnTool instance = new LootSpawnTool();

    @Override
    protected void onSuccessfulBlockClick(Player player, DungeonGame game, Block block, ClickType click) {
        DungeonMap map = game.getMapRotator().getCurrentMap();
        LootPoint point = map.getLootSpawnPoint(block.getLocation());

        if (click == ClickType.RIGHT && point != null) {
            LootSpawnMenu.openConfigMenu(player, point);
            return;
        }

        boolean added = map.toggleLootSpawnPoint(block.getLocation());

        Messenger.success(player, "Successfully " + (added ? "&2added&7" : "&cremoved&7") + " a loot spawn point. Click to configure");
    }

    @Override
    protected List<Location> getGamePoints(Player player, DungeonGame game) {
        return Common.convert(PlayerUtil.getMapSafe(player).getLootPoints(), LootPoint::getLocation);
    }

    @Override
    protected String getBlockName(Block block, Player player) {
        return "Loot Spawn";
    }

    @Override
    protected CompMaterial getBlockMask(Block block, Player player) {
        return CompMaterial.DIAMOND_BLOCK;
    }

    @Override
    public ItemStack getItem() {
        return ItemCreator.of(CompMaterial.CHEST, "&l&3LOOT SPAWN TOOL",
                "",
                "&b<< &fLeft click &7– &fTo remove or add a point",
                "&fRight click a point &7– &fTo open configuration menu &b>>").makeMenuTool();
    }
}
