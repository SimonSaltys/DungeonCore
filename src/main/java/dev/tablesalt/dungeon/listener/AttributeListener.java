package dev.tablesalt.dungeon.listener;

import dev.tablesalt.dungeon.event.PlayerGainGoldEvent;
import dev.tablesalt.dungeon.item.ItemAttribute;
import dev.tablesalt.dungeon.item.Tier;
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


}
