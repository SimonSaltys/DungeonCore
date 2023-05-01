package dev.tablesalt.dungeon.game.helpers;

import dev.tablesalt.dungeon.game.DungeonGame;
import dev.tablesalt.gamelib.game.helpers.Game;
import dev.tablesalt.gamelib.game.helpers.Stopper;

public class DungeonStopper extends Stopper {

    private final DungeonGame game;

    public DungeonStopper(DungeonGame game) {
        super(game);

        this.game = game;
    }





}
