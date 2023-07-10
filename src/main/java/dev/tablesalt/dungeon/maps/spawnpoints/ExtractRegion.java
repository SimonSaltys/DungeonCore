package dev.tablesalt.dungeon.maps.spawnpoints;


import dev.tablesalt.dungeon.game.DungeonGame;
import dev.tablesalt.dungeon.util.MessageUtil;
import dev.tablesalt.dungeon.util.PlayerUtil;
import dev.tablesalt.gamelib.game.utils.GameUtil;
import dev.tablesalt.gamelib.game.utils.SimpleRunnable;
import dev.tablesalt.gamelib.players.PlayerCache;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.model.ConfigSerializable;
import org.mineacademy.fo.model.Countdown;
import org.mineacademy.fo.model.SimpleTime;
import org.mineacademy.fo.remain.Remain;
import org.mineacademy.fo.visual.VisualizedRegion;

public class ExtractRegion implements ConfigSerializable {
    @Getter
    private VisualizedRegion region;
    @Getter
    private SimpleTime timeToWait;
    @Getter @Setter
    private boolean active;

    public ExtractRegion() {
        this(new VisualizedRegion());
    }
    public ExtractRegion(VisualizedRegion region) {
        this.region = region;
        this.timeToWait = SimpleTime.fromSeconds(10);
    }

    @Override
    public SerializedMap serialize() {
        return SerializedMap.ofArray(
                "region", region,
                "time", timeToWait
        );
    }

    public void startExtractionFor(Player player) {
        PlayerCache cache = PlayerCache.from(player);
        DungeonGame game = (DungeonGame) cache.getGameIdentifier().getCurrentGame();
        boolean isExtracting = cache.getTagger().getBooleanTagSafe("Extracting");

        if (isExtracting)
            return;

        cache.getTagger().setPlayerTag("Extracting", true);
        ExtractionCountdown extractionCountdown = new ExtractionCountdown(cache,game);
        extractionCountdown.launch();

    }

    public static ExtractRegion deserialize(SerializedMap map) {
       ExtractRegion point = new ExtractRegion();
       point.region = map.get("region",VisualizedRegion.class);
       point.timeToWait = map.get("time",SimpleTime.class);

       return point;
    }

    private class ExtractionCountdown extends SimpleRunnable {

        private final Player player;
        private final PlayerCache cache;
        private final DungeonGame game;

        protected ExtractionCountdown(PlayerCache cache, DungeonGame game) {
            super(timeToWait);

            this.cache = cache;
            this.player = cache.toPlayer();
            this.game = game;
        }

        @Override
        protected void onTick() {
            if (cache.getStateIdentifier().isDead() || !region.isWithin(player.getLocation())) {
                cache.getTagger().setPlayerTag("Extracting", false);
                MessageUtil.clearTitle(player);
                cancel();
            } else {
                Remain.sendTitle(player,0,40,0,"&6Extracting in", Common.plural(getTimeLeft(),"seconds"));
            }
        }

        @Override
        protected void onEnd() {
            Remain.sendTitle(player,0,40,0,"&6You Extracted!", "");
            game.getLeaver().leavePlayerBecauseExtracted(player);
        }
    }
}
