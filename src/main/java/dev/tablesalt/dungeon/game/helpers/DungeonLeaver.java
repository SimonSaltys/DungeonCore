package dev.tablesalt.dungeon.game.helpers;

import dev.tablesalt.dungeon.DungeonSettings;
import dev.tablesalt.dungeon.game.DungeonGame;
import dev.tablesalt.dungeon.maps.DungeonMap;
import dev.tablesalt.dungeon.maps.spawnpoints.ExtractRegion;
import dev.tablesalt.dungeon.util.DungeonLeaveReason;
import dev.tablesalt.gamelib.game.helpers.PlayerLeaver;
import org.bukkit.entity.Player;

public class DungeonLeaver extends PlayerLeaver {
    private final DungeonGame game;

    public DungeonLeaver(DungeonGame game) {
        super(game);

        this.game = game;
    }

    public void leavePlayerBecauseExtracted(Player player) {
        leavePlayer(player, DungeonLeaveReason.EXTRACTED);
        player.teleport(DungeonSettings.getInstance().getEnchantingLocation().clone().add(-5, 0, 0));


    }

    public void leavePlayerBecauseDied(Player player) {
        //todo drop items and leave their dead body behind using nms?
    }

    @Override
    protected void onGameLeave(Player player) {
        hideParticles(player);
    }

    private void hideParticles(Player player) {
        DungeonMap map = game.getMapRotator().getCurrentMap();

        for (ExtractRegion region : map.getExtractRegions())
            if (region.getRegion().canSeeParticles(player))
                region.getRegion().hideParticles(player);
    }


}
