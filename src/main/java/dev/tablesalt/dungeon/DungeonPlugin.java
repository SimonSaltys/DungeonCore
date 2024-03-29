package dev.tablesalt.dungeon;


import dev.tablesalt.dungeon.configitems.LootChance;
import dev.tablesalt.dungeon.database.DungeonCache;
import dev.tablesalt.dungeon.database.EnchantableItem;
import dev.tablesalt.dungeon.database.MariaDatabase;
import dev.tablesalt.dungeon.game.DungeonGame;
import dev.tablesalt.dungeon.item.ItemAttribute;
import dev.tablesalt.dungeon.listener.*;
import dev.tablesalt.dungeon.menu.MenuListener;
import dev.tablesalt.dungeon.model.effects.Effects;
import dev.tablesalt.dungeon.util.ScoreBoardUtil;
import dev.tablesalt.dungeon.util.TBSItemUtil;
import dev.tablesalt.gamelib.game.helpers.Game;
import dev.tablesalt.gamelib.game.helpers.GameListener;
import dev.tablesalt.gamelib.game.types.GameTypeList;
import dev.tablesalt.gamelib.game.types.Type;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.mineacademy.fo.remain.Remain;

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
        for (Game game : Game.getGames())
            game.getStopper().stop();

        MariaDatabase.getInstance().saveForAll();
        DungeonCache.purge();
        Effects.disable();
    }

    /**
     * Executes the operations that need to be done on server start and reload.
     */
    private void initialization() {
        GameTypeList.getInstance().addType(new Type<>("dungeon", DungeonGame.class));
        LootChance.loadChances();
        ItemAttribute.registerAttributes();
        ScoreBoardUtil.displayHubScoreboardToAll();

        MariaDatabase.getInstance().connect();
        MariaDatabase.getInstance().loadForAll();
        Effects.loadEffects();

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
