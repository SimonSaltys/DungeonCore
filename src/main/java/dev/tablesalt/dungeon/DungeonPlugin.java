package dev.tablesalt.dungeon;


import dev.tablesalt.dungeon.database.RedisDatabase;
import dev.tablesalt.dungeon.game.DungeonGame;
import dev.tablesalt.dungeon.item.ItemAttribute;
import dev.tablesalt.dungeon.listener.DatabaseListener;
import dev.tablesalt.gamelib.game.types.GameTypeList;
import dev.tablesalt.gamelib.game.types.Type;
import org.bukkit.entity.Player;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.mineacademy.fo.remain.Remain;

public final class DungeonPlugin extends SimplePlugin {


    @Override
    public void onPluginStart() {
    }

    @Override
    protected void onReloadablesStart() {
        GameTypeList.getInstance().addType(new Type<>("dungeon", DungeonGame.class));
        RedisDatabase.getInstance().connect();
        ItemAttribute.registerAttributes();

        registerDungeonEvents();

    }

    @Override
    public void onPluginStop() {

        for (Player player : Remain.getOnlinePlayers())
            RedisDatabase.getInstance().saveItems(player);


        RedisDatabase.getInstance().disable();
    }

    private void registerDungeonEvents() {
        registerEvents(new DatabaseListener());
    }
}
