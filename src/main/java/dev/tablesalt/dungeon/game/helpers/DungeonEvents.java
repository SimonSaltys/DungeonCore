package dev.tablesalt.dungeon.game.helpers;

import dev.tablesalt.dungeon.game.DungeonGame;
import dev.tablesalt.dungeon.nms.PlayerCorpse;
import dev.tablesalt.dungeon.util.sound.TBSSound;
import dev.tablesalt.gamelib.game.helpers.GameEvents;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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
    protected void onPlayerKillPlayer(Player killer, Player victim) {
        TBSSound.Kill.getInstance().playTo(killer);
    }

    @Override
    protected void onPvP(Player attacker, Player victim, EntityDamageByEntityEvent event) {

    }

    @Override
    protected void onBlockBreak(Player player, Block block, BlockBreakEvent event) {
        event.setCancelled(true);
    }

    @Override
    protected void onBlockPlace(Player player, Block block, BlockPlaceEvent event) {
        event.setCancelled(true);
    }

    @Override
    protected DungeonGame getGame() {
        return (DungeonGame) game;
    }
}
