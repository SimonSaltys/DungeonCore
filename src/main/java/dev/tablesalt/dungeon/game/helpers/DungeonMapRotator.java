package dev.tablesalt.dungeon.game.helpers;

import dev.tablesalt.dungeon.maps.DungeonMap;
import dev.tablesalt.gamelib.game.helpers.Game;
import dev.tablesalt.gamelib.game.helpers.MapRotator;

public class DungeonMapRotator extends MapRotator {
    public DungeonMapRotator(Game game) {
        super(game);
    }

    @Override
    public DungeonMap getCurrentMap() {
        return (DungeonMap) super.getCurrentMap();
    }
}
