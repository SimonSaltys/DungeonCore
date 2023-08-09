package dev.tablesalt.dungeon.event;

import dev.tablesalt.dungeon.database.DungeonCache;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerGainGoldEvent extends Event {
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    @Getter
    private final Player player;
    @Getter
    private final double amountGained;

    public PlayerGainGoldEvent(Player player, double amountGained) {
        this.player = player;
        this.amountGained = amountGained;
    }

    public void addMoreGold(double amount) {
        DungeonCache cache = DungeonCache.from(player);
        cache.moneyAmount += amount;
    }


    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }


}
