package dev.tablesalt.dungeon;


import dev.tablesalt.dungeon.configitems.LootChance;
import dev.tablesalt.dungeon.database.DungeonCache;
import dev.tablesalt.dungeon.database.MariaDatabase;
import dev.tablesalt.dungeon.game.DungeonGame;
import dev.tablesalt.dungeon.item.ItemAttribute;
import dev.tablesalt.dungeon.listener.*;
import dev.tablesalt.dungeon.menu.MenuListener;
import dev.tablesalt.gamelib.game.helpers.Game;
import dev.tablesalt.gamelib.game.helpers.GameListener;
import dev.tablesalt.gamelib.game.types.GameTypeList;
import dev.tablesalt.gamelib.game.types.Type;
import org.mineacademy.fo.plugin.SimplePlugin;

public final class DungeonPlugin extends SimplePlugin {


    @Override
    public void onPluginStart() {

    }

    @Override
    protected void onReloadablesStart() {
        initialization();

        DungeonSettings.getInstance().onLoad();
    }

    @Override
    public void onPluginStop() {
        DungeonCache.purge();
        for (Game game : Game.getGames())
            game.getStopper().stop();
    }

    /**
     * Holds the operations that need to be done on server start and reload.
     */
    private void initialization() {
        GameTypeList.getInstance().addType(new Type<>("dungeon", DungeonGame.class));
        LootChance.loadChances();
        MariaDatabase.getInstance().connect();

        ItemAttribute.registerAttributes();

        registerDungeonEvents();

    }

    private void registerDungeonEvents() {
        registerEvents(new DatabaseListener());
        registerEvents(new OutOfDungeonListener());
        registerEvents(new InDungeonListener());
        registerEvents(new MenuListener());
        registerEvents(new GameListener());
        registerEvents(new AttributeListener());
        registerEvents(new CommandsListener());
    }
}
