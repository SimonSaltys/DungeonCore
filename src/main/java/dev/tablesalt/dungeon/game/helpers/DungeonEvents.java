package dev.tablesalt.dungeon.game.helpers;

import dev.tablesalt.dungeon.game.DungeonGame;
import dev.tablesalt.dungeon.nms.PlayerCorpse;
import dev.tablesalt.gamelib.game.helpers.GameEvents;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public class DungeonEvents extends GameEvents {
    public DungeonEvents(DungeonGame game) {
        super(game);
    }


    @Override
    protected void onDeath(Player player, EntityDamageEvent event) {
        new PlayerCorpse(player).makeCorpse();
        getGame().getLeaver().leavePlayerBecauseDied(player);
    }

    @Override
    protected DungeonGame getGame() {
        return (DungeonGame) game;
    }
}
