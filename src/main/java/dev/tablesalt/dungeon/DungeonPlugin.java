package dev.tablesalt.dungeon;


import dev.tablesalt.dungeon.database.RedisDatabase;
import dev.tablesalt.dungeon.game.DungeonGame;
import dev.tablesalt.dungeon.item.AttributeListener;
import dev.tablesalt.dungeon.listener.DatabaseListener;
import dev.tablesalt.gamelib.game.types.GameTypeList;
import dev.tablesalt.gamelib.game.types.Type;
import org.mineacademy.fo.plugin.SimplePlugin;

public final class DungeonPlugin extends SimplePlugin {


    @Override
    public void onPluginStart() {
    }

    @Override
    protected void onReloadablesStart() {
        GameTypeList.getInstance().addType(new Type<>("dungeon", DungeonGame.class));

        RedisDatabase.getInstance().connect();

        registerDungeonEvents();

    }

    @Override
    public void onPluginStop() {
        RedisDatabase.getInstance().disable();
    }

    private void registerDungeonEvents() {
        registerEvents(new DatabaseListener());
        registerEvents(new AttributeListener());
    }
}
