package dev.tablesalt.dungeon.listener;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import dev.tablesalt.dungeon.database.EnchantableItem;
import dev.tablesalt.dungeon.event.PlayerGainGoldEvent;
import dev.tablesalt.dungeon.item.ItemAttribute;
import dev.tablesalt.dungeon.item.Tier;
import dev.tablesalt.gamelib.players.PlayerCache;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;

public class AttributeListener implements Listener {

    @EventHandler
    public void onPlayerGainGold(PlayerGainGoldEvent event) {
        Map<ItemAttribute, Integer> itemItemAttributeMap = ItemAttribute.getAttributesOnMainHandItem(event.getPlayer());

        for (ItemAttribute attribute : itemItemAttributeMap.keySet())
            attribute.onGoldGain(event.getPlayer(), event.getAmountGained(), Tier.fromInteger(itemItemAttributeMap.get(attribute)), event);
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
            for (ItemAttribute attribute : armorToRemove.getAttributeTierMap().keySet())
                attribute.onArmorTakeOff(player, Tier.fromInteger(armorToRemove.getAttributeTierMap().get(attribute)), event);

        if (armorToEquip != null)
            for (ItemAttribute attribute : armorToEquip.getAttributeTierMap().keySet())
                attribute.onArmorEquip(player, Tier.fromInteger(armorToEquip.getAttributeTierMap().get(attribute)), event);


    }


}
