package dev.tablesalt.dungeon.tools;

import dev.tablesalt.dungeon.game.DungeonGame;
import dev.tablesalt.dungeon.maps.DungeonMap;
import dev.tablesalt.dungeon.maps.MonsterSpawnPoint;
import dev.tablesalt.dungeon.menu.MonsterPointMenu;
import dev.tablesalt.gameLib.lib.Common;
import dev.tablesalt.gameLib.lib.menu.model.ItemCreator;
import dev.tablesalt.gameLib.lib.remain.CompMaterial;
import dev.tablesalt.gameLib.lib.remain.CompSound;
import dev.tablesalt.gamelib.tools.GameTool;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class MobSpawnTool extends GameTool<DungeonGame> {
    @Getter
    private static final MobSpawnTool instance = new MobSpawnTool();
    @Override
    protected CompMaterial getBlockMask(Block block, Player player) {
        return CompMaterial.SPAWNER;
    }

    @Override
    protected void onSuccessfulBlockClick(Player player, DungeonGame game, Block block, ClickType type) {
        DungeonMap map = game.getMapRotator().getCurrentMap();

        if (map == null)
            return;

        if (type == ClickType.LEFT)
            map.toggleLocationInList(player, new MonsterSpawnPoint(block.getLocation()), map.getMonsterSpawnPoints());

        else if (type == ClickType.RIGHT) {
            MonsterSpawnPoint spawnPoint = (MonsterSpawnPoint) map.hasPointWithLocation(block.getLocation(),map.getMonsterSpawnPoints());
            if (spawnPoint != null)
                MonsterPointMenu.openSelectMenu(player,spawnPoint);
        }

       }
    @Override
    protected List<Location> getGamePoints(Player player, DungeonGame game) {
        DungeonMap map = game.getMapRotator().getCurrentMap();

        if (map != null)
            return Common.convert(map.getMonsterSpawnPoints(), MonsterSpawnPoint::getLocation);

        return null;
    }

    @Override
    protected String getBlockName(Block block, Player player) {
        return "[Mob Spawn Point]";
    }

    @Override
    public ItemStack getItem() {
        return ItemCreator.of(CompMaterial.SPAWNER, "&l&3MOB SPAWN TOOL",
                "",
                "&b<< &fLeft click &7– &fTo remove or add a point",
                "&fRight click a point &7– &fTo open configuration menu &b>>").makeMenuTool();
    }
}
