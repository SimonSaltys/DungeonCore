package dev.tablesalt.dungeon.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.tablesalt.dungeon.event.PlayerGainGoldEvent;
import dev.tablesalt.dungeon.util.TBSItemUtil;
import dev.tablesalt.dungeon.util.TBSPlayerUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.SerializeUtil;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.remain.Remain;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Holds information that is useful when
 * a player is on the server. This information
 * is highly volatile, and it is recommended to save
 * often to a database if you want to persist data.
 */
public class DungeonCache {

    private static final Map<UUID, DungeonCache> cacheMap = new HashMap<>();
    @Getter
    private final UUID uniqueId;
    @Getter
    private final String playerName;

    private boolean isInCombat = false;

    @Getter
    @Setter
    private EnchantableItem itemInEnchanter;

    public double moneyAmount;


    private DungeonCache(String name, UUID uniqueId) {
        this.uniqueId = uniqueId;
        this.playerName = name;

    }

    public void setInCombat(boolean combat) {
        this.isInCombat = combat;
    }

    public boolean isInCombat() {
        return isInCombat;
    }



    /*----------------------------------------------------------------*/
    /* MONEY METHODS */
    /*----------------------------------------------------------------*/


    public void giveMoney(double amount) {
        moneyAmount += amount;
        Common.callEvent(new PlayerGainGoldEvent(toPlayer(), amount));
    }

    public void takeMoney(int amount) {
        moneyAmount = moneyAmount - amount;

        if (moneyAmount < 0)
            moneyAmount = 0;
    }

    public double getMoney() {
        return moneyAmount;
    }


    @Nullable
    public Player toPlayer() {
        Player player = Remain.getPlayerByUUID(this.uniqueId);
        return player != null && player.isOnline() ? player : null;
    }

    public static SerializedMap toSerializedMap(Player player) {
        DungeonCache cache = DungeonCache.from(player);

        return SerializedMap.ofArray(
                "Money", cache.getMoney(),
                "Normal_Items", SerializeUtil.serialize(SerializeUtil.Mode.JSON,
                        TBSPlayerUtil.getItemsInSlots(player)));
    }
    /*----------------------------------------------------------------*/
    /* STATIC METHODS */
    /*----------------------------------------------------------------*/

    /**
     * Return or create new player cache for the given player
     *
     * @param player
     * @return
     */
    public static DungeonCache from(Player player) {
        synchronized (cacheMap) {
            final UUID uniqueId = player.getUniqueId();
            final String playerName = player.getName();

            DungeonCache cache = cacheMap.get(uniqueId);

            if (cache == null) {
                cache = new DungeonCache(playerName, uniqueId);

                cacheMap.put(uniqueId, cache);
            }

            return cache;
        }
    }

    public static void purge() {
        cacheMap.clear();
    }


}
