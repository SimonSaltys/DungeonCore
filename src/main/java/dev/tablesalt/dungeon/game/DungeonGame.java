package dev.tablesalt.dungeon.game;

import dev.tablesalt.dungeon.DungeonPlugin;
import dev.tablesalt.dungeon.game.helpers.*;
import dev.tablesalt.dungeon.game.scoreboard.DungeonScoreboard;
import dev.tablesalt.dungeon.maps.DungeonMap;
import dev.tablesalt.gamelib.game.helpers.*;
import dev.tablesalt.gamelib.game.map.GameMap;
import dev.tablesalt.gamelib.game.types.Type;
import org.bukkit.plugin.Plugin;

public class DungeonGame extends Game {
    public DungeonGame(String name) {
        super(name, new Type<>("dungeon", DungeonGame.class));
    }

    @Override
    public Type<GameMap> getGameMapType() {
        return new Type<>("dungeon_map", DungeonMap.class);
    }

    @Override
    public Plugin getOwningPlugin() {
        return DungeonPlugin.getInstance();
    }

    /*----------------------------------------------------------------*/
    /* Custom Classes Overrides */
    /*----------------------------------------------------------------*/

    @Override
    protected MapRotator compileMapRotator() {
        return new DungeonMapRotator(this);
    }

    @Override
    protected DungeonScoreboard compileScoreboard() {
        return new DungeonScoreboard(this);
    }

    @Override
    protected Starter compileStarter() {
        return new DungeonStarter(this);
    }

    @Override
    protected Stopper compileStopper() {
        return new DungeonStopper(this);
    }

    @Override
    protected PlayerLeaver compileLeaver() {
        return new DungeonLeaver(this);
    }

    @Override
    protected GameHeartbeat compileHeartbeat() {
        return new DungeonHeartbeat(this);
    }

    @Override
    protected PlayerJoiner compileJoiner() {
        return new DungeonJoiner(this);
    }

    @Override
    protected GameEvents compileGameEvents() {
        return new DungeonEvents(this);
    }

    public DungeonMapRotator getMapRotator() {
        return (DungeonMapRotator) super.getMapRotator();
    }


    /*----------------------------------------------------------------*/
    /* Custom Classes Getters */
    /*----------------------------------------------------------------*/

    @Override
    public DungeonScoreboard getScoreboard() {
        return (DungeonScoreboard) super.getScoreboard();
    }

    public DungeonStarter getStarter() {
        return (DungeonStarter) super.getStarter();
    }

    @Override
    public DungeonStopper getStopper() {
        return (DungeonStopper) super.getStopper();
    }

    public DungeonHeartbeat getHeartbeat() {
        return (DungeonHeartbeat) super.getHeartbeat();
    }

    public DungeonLeaver getLeaver() {
        return (DungeonLeaver) super.getPlayerLeaver();
    }
}
