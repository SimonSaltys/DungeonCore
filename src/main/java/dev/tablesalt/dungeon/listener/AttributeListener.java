package dev.tablesalt.dungeon.listener;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import dev.tablesalt.dungeon.database.EnchantableItem;
import dev.tablesalt.dungeon.event.PlayerGainGoldEvent;
import dev.tablesalt.dungeon.item.ItemAttribute;
import dev.tablesalt.dungeon.item.Tier;
import dev.tablesalt.dungeon.util.TBSItemUtil;
import dev.tablesalt.gamelib.event.PlayerJoinGameEvent;
import dev.tablesalt.gamelib.players.PlayerCache;
import org.bukkit.entity.Cod;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.function.BiConsumer;

public class AttributeListener implements Listener {

    @EventHandler
    public void onPlayerGainGold(PlayerGainGoldEvent event) {
        Player player = event.getPlayer();
        EnchantableItem item = EnchantableItem.fromItemStack(player.getInventory().getItemInMainHand());

        if (item == null)
            return;

        item.forAllAttributes(((itemAttribute, tier)
                -> itemAttribute.onGoldGain(player,event.getAmountGained(),tier,event)));
    }

    @EventHandler
    public void onArmorEquip(PlayerArmorChangeEvent event) {
        Player player = event.getPlayer();

        PlayerCache cache = PlayerCache.from(player);

        if (!cache.getGameIdentifier().hasGame())
            return;

        EnchantableItem armorToEquip = EnchantableItem.fromItemStack(event.getNewItem());
        EnchantableItem armorToRemove = EnchantableItem.fromItemStack(event.getOldItem());

        if (armorToEquip == null && armorToRemove != null)
            armorToRemove.forAllAttributes(((itemAttribute, tier)
                    -> itemAttribute.onArmorTakeOff(player, tier, event)));

        if (armorToEquip != null)
            armorToEquip.forAllAttributes(((itemAttribute, tier)
                    -> itemAttribute.onArmorEquip(player, tier, event)));
    }
    @EventHandler
    public void onGameJoin(PlayerJoinGameEvent event) {
        Player player = event.getPlayer();

        ItemStack[] allContents = player.getInventory().getContents();

        for (ItemStack item : allContents) {
            EnchantableItem enchantableItem = EnchantableItem.fromItemStack(item);

            if (enchantableItem == null)
                continue;

            enchantableItem.forAllAttributes((itemAttribute, tier)
                    -> itemAttribute.onPlayerJoinGame(player, tier, event));
        }

    }


}
