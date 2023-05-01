package dev.tablesalt.dungeon.util;

import dev.tablesalt.dungeon.game.DungeonGame;
import dev.tablesalt.dungeon.maps.DungeonMap;
import dev.tablesalt.gameLib.lib.Valid;
import dev.tablesalt.gamelib.players.PlayerCache;
import io.lumine.mythic.bukkit.utils.lib.jooq.impl.QOM;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;

@UtilityClass
public class PlayerUtil {

    public DungeonMap getMapSafe(Player player) {
        PlayerCache cache = PlayerCache.from(player);
        DungeonGame game = (DungeonGame) cache.getGameIdentifier().getCurrentGame();

        Valid.checkNotNull(game, "Player is not in a game!");
        DungeonMap map = game.getMapRotator().getCurrentMap();

        Valid.checkNotNull(map, "Player is not in a map!");
        return map;
    }



}
