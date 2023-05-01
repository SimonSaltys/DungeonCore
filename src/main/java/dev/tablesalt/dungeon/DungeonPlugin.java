package dev.tablesalt.dungeon;


import dev.tablesalt.dungeon.game.DungeonGame;
import dev.tablesalt.gameLib.lib.Common;
import dev.tablesalt.gamelib.GameLib;
import dev.tablesalt.gamelib.game.types.GameTypeList;
import dev.tablesalt.gamelib.game.types.Type;

public final class DungeonPlugin extends GameLib {

    @Override
    public void onPluginStart() {
        Common.log("Dungeon is enabled!");
    }

    @Override
    protected void onReloadablesStart() {
        GameTypeList.getInstance().addType(new Type<>("dungeon", DungeonGame.class));
    }

    @Override
    public void onPluginStop() {
        // Plugin shutdown logic
    }
}
