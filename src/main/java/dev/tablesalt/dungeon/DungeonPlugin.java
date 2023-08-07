package dev.tablesalt.dungeon;


import dev.tablesalt.dungeon.configitems.LootChance;
import dev.tablesalt.dungeon.database.DungeonCache;
import dev.tablesalt.dungeon.database.MariaDatabase;
import dev.tablesalt.dungeon.game.DungeonGame;
import dev.tablesalt.dungeon.item.ItemAttribute;
import dev.tablesalt.dungeon.listener.DatabaseListener;
import dev.tablesalt.dungeon.listener.InDungeonListener;
import dev.tablesalt.dungeon.listener.OutOfDungeonListener;
import dev.tablesalt.dungeon.menu.MenuListener;
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
    }
}
