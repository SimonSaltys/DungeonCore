package dev.tablesalt.dungeon.game.helpers;

import dev.tablesalt.dungeon.game.DungeonGame;
import dev.tablesalt.dungeon.util.MessageUtil;
import dev.tablesalt.gameLib.lib.model.RandomNoRepeatPicker;
import dev.tablesalt.gamelib.game.enums.GameJoinMode;
import dev.tablesalt.gamelib.game.helpers.Game;
import dev.tablesalt.gamelib.game.helpers.Starter;
import dev.tablesalt.gamelib.game.utils.GameUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class DungeonStarter extends Starter {
    private final DungeonGame game;
    public DungeonStarter(DungeonGame game) {
        super(game);

        this.game = game;
    }

    @Override
    protected void onGameStart() {
        teleportPlayers();
        broadcastInfo();
        //todo randomize extracts, spawn loot crates, etc.
    }


    private void broadcastInfo() {
        game.getGameBroadcaster().broadcast(MessageUtil.makeInfo("&7The Dungeon Begins! &e&lAdventurers: &r&7" + game.getPlayerGetter().getPlayers(GameJoinMode.PLAYING).size() + "." ));
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
}
