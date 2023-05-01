package dev.tablesalt.dungeon.tools;

import dev.tablesalt.dungeon.game.DungeonGame;
import dev.tablesalt.dungeon.maps.DungeonMap;
import dev.tablesalt.gameLib.lib.Common;
import dev.tablesalt.gameLib.lib.menu.model.ItemCreator;
import dev.tablesalt.gameLib.lib.remain.CompMaterial;
import dev.tablesalt.gameLib.lib.settings.FileConfig;
import dev.tablesalt.gamelib.game.map.GameMap;
import dev.tablesalt.gamelib.tools.GameTool;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class LocationListTool extends GameTool<DungeonGame> {

    protected abstract FileConfig.LocationList getLocationList(DungeonGame game);
    protected abstract ItemStack getTool();
    protected abstract int getMaxPoints(DungeonMap game);

    @Override
    protected final void onSuccessfulBlockClick(Player player, DungeonGame game, Block block, ClickType type) {
        FileConfig.LocationList points = getLocationList(game);
        DungeonMap map = game.getMapRotator().getCurrentMap();

        if (map == null) {
            Common.tellNoPrefix(player, "&cNo map currently found for game &f" + game.getName() + "&c.");
            return;
        }

        if (points.size() >= getMaxPoints(map)) {
            Common.tellNoPrefix(player, "&7Maximum number of spawn points reached.");

            if (points.hasLocation(block.getLocation())) {
                points.remove(block.getLocation());
                Common.tellNoPrefix(player, "&7Spawn point &cremoved&7.");
            }
            return;
        }
        boolean success = points.toggle(block.getLocation());
        Common.tellNoPrefix(player, "&7Spawn point " + (success ? "&aadded" : "&cremoved") + "&7.");
    }
    @Override
    protected final List<Location> getGamePoints(Player player, DungeonGame game) {
        return getLocationList(game).getLocations();
    }

    @Override
    protected final void onSuccessfulAirClick(Player player, DungeonGame game, ClickType type) {
        super.onSuccessfulAirClick(player, game, type);
    }


    @Override
    public final ItemStack getItem() {
        return ItemCreator.of(getTool()).lore("", "&b<< &fClick– To Place or remove point &b>>").makeMenuTool();
    }
}
