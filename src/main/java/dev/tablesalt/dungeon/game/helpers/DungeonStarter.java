package dev.tablesalt.dungeon.game.helpers;

import dev.tablesalt.dungeon.game.DungeonGame;
import dev.tablesalt.dungeon.maps.DungeonMap;
import dev.tablesalt.dungeon.maps.spawnpoints.ExtractRegion;
import dev.tablesalt.dungeon.util.DungeonUtil;
import dev.tablesalt.gamelib.exception.GameException;
import dev.tablesalt.gamelib.game.enums.GameJoinMode;
import dev.tablesalt.gamelib.game.helpers.Starter;
import dev.tablesalt.gamelib.game.utils.GameUtil;
import dev.tablesalt.gamelib.game.utils.MessageUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.model.RandomNoRepeatPicker;

import java.util.Collections;
import java.util.List;

public class DungeonStarter extends Starter {
    private final DungeonGame game;

    public DungeonStarter(DungeonGame game) {
        super(game);

        this.game = game;
    }

    @Override
    protected void onGameStart() {
        broadcastInfo();
        activateExtractLocations();
        DungeonUtil.spawnLoot(game);

//        teleportPlayers();
    }


    private void broadcastInfo() {
        game.getGameBroadcaster().broadcast(MessageUtil.makeInfo("&7The Dungeon Begins! &e&lAdventurers: &r&7"
                + game.getPlayerGetter().getPlayers(GameJoinMode.PLAYING).size() + "."));
    }

    private void teleportPlayers() {
        RandomNoRepeatPicker<Location> spawnPoints = RandomNoRepeatPicker.newPicker(Location.class);
        spawnPoints.setItems(game.getMapRotator().getCurrentMap().getPlayerSpawnPoints());

        getGame().getPlayerGetter().forEach(cache -> {
            Player player = cache.toPlayer();

            Location location = spawnPoints.pickRandom(player);
            GameUtil.teleport(player, location);

        }, GameJoinMode.PLAYING);

    }

    private void activateExtractLocations() {
        DungeonMap map = game.getMapRotator().getCurrentMap();

        List<ExtractRegion> regions = map.getExtractRegions();
        Collections.shuffle(regions);

        if (regions.size() < map.getExtractRegionsToActivate())
            Common.throwError(new GameException(), "Trying to activate " + map.getExtractRegionsToActivate()
                    + " and there are only " + regions.size() + " available");

        for (int i = 0; i < map.getExtractRegionsToActivate(); i++)
            regions.get(i).setActive(true);
    }


}
