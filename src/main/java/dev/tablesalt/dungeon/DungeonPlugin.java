package dev.tablesalt.dungeon;


import dev.tablesalt.dungeon.database.RedisDatabase;
import dev.tablesalt.dungeon.game.DungeonGame;
import dev.tablesalt.dungeon.item.AttributeListener;
import dev.tablesalt.gamelib.game.types.GameTypeList;
import dev.tablesalt.gamelib.game.types.Type;
import org.mineacademy.fo.plugin.SimplePlugin;

public final class DungeonPlugin extends SimplePlugin {


    @Override
    public void onPluginStart() {
        RedisDatabase.getInstance().connect();
    }

    @Override
    protected void onReloadablesStart() {
        GameTypeList.getInstance().addType(new Type<>("dungeon", DungeonGame.class));
        registerEvents(new AttributeListener());

    }

    @Override
    public void onPluginStop() {
        // Plugin shutdown logic
    }
}
