package dev.tablesalt.dungeon;


import dev.tablesalt.dungeon.game.DungeonGame;
import dev.tablesalt.gamelib.GameLib;
import dev.tablesalt.gamelib.game.types.GameTypeList;
import dev.tablesalt.gamelib.game.types.Type;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.plugin.SimplePlugin;

public final class DungeonPlugin extends SimplePlugin {


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
