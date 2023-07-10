package dev.tablesalt.dungeon.tools;

import dev.tablesalt.dungeon.game.DungeonGame;
import dev.tablesalt.dungeon.maps.DungeonMap;
import dev.tablesalt.dungeon.maps.spawnpoints.MonsterPoint;
import dev.tablesalt.dungeon.maps.spawnpoints.SpawnPoint;
import dev.tablesalt.dungeon.menu.MonsterSpawnMenu;
import dev.tablesalt.dungeon.util.PlayerUtil;
import dev.tablesalt.gamelib.tools.GameTool;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class MobSpawnTool extends GameTool<DungeonGame> {
    @Getter
    private static final MobSpawnTool instance = new MobSpawnTool();
    @Override
    protected CompMaterial getBlockMask(Block block, Player player) {
        return CompMaterial.SPAWNER;
    }

    @Override
    protected void onSuccessfulBlockClick(Player player, DungeonGame game, Block block, ClickType click) {
        DungeonMap map = game.getMapRotator().getCurrentMap();
        SpawnPoint point = map.getMonsterSpawnPoint(block.getLocation());

        if (click == ClickType.RIGHT && point instanceof MonsterPoint monsterPoint) {
            MonsterSpawnMenu.openConfigMenu(player, monsterPoint);
            return;
        }

        boolean added = map.toggleMonsterSpawnPoint(block.getLocation());

        Messenger.success(player, "Successfully " + (added ? "&2added&7" : "&cremoved&7") + " a monster spawn point. Click to configure");
       }

    @Override
    protected List<Location> getGamePoints(Player player, DungeonGame game) {

            return Common.convert(PlayerUtil.getMapSafe(player).getMonsterPoints(),
                    MonsterPoint::getLocation);


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
