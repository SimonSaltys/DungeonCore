package dev.tablesalt.dungeon.game.helpers;

import dev.tablesalt.dungeon.database.EnchantableItem;
import dev.tablesalt.dungeon.database.Keys;
import dev.tablesalt.dungeon.game.DungeonGame;
import dev.tablesalt.dungeon.item.ItemAttribute;
import dev.tablesalt.dungeon.item.Tier;
import dev.tablesalt.dungeon.nms.PlayerCorpse;
import dev.tablesalt.dungeon.util.DungeonUtil;
import dev.tablesalt.dungeon.util.TBSItemUtil;
import dev.tablesalt.dungeon.util.sound.TBSSound;
import dev.tablesalt.gamelib.game.helpers.GameEvents;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.PlayerUtil;
import org.mineacademy.fo.RandomUtil;

import java.util.Map;

public class DungeonEvents extends GameEvents {
    public DungeonEvents(DungeonGame game) {
        super(game);
    }


    @Override
    protected void onDeath(Player player, EntityDamageEvent event) {
        PlayerCorpse foundCorpse = PlayerCorpse.getFromPlayerName(player.getName());
        if (foundCorpse != null)
            PlayerCorpse.removeCorpse(foundCorpse);

        Common.runLater(2, () -> {
            new PlayerCorpse(player).makeCorpse();
            DungeonUtil.teleportToLobby(player, getGame());
            PlayerUtil.normalize(player, true);

        });
    }

    @Override
    protected void onPlayerKillPlayer(Player killer, Player victim) {
        TBSSound.Kill.getInstance().playTo(killer);
        game.getGameBroadcaster().broadcast("&c&lDEATH!&r " + victim.getName() + " was " + RandomUtil.nextItem(Keys.KILL_VERB) + " by " + killer.getName());
    }


    @Override
    protected void onBlockBreak(Player player, Block block, BlockBreakEvent event) {
        event.setCancelled(true);
    }

    @Override
    protected void onBlockPlace(Player player, Block block, BlockPlaceEvent event) {
        event.setCancelled(true);
    }


    /*----------------------------------------------------------------*/
    /* ATTRIBUTE RELATED METHOD CALLING */
    /*----------------------------------------------------------------*/


    @Override
    protected void onDamaged(Player victim, EntityDamageEvent event) {
        callOnDamaged(victim, null, event);
    }


    @Override
    protected void onPvP(Player attacker, Player victim, EntityDamageByEntityEvent event) {
        Map<ItemAttribute, Integer> itemItemAttributeMap = ItemAttribute.getAttributesOnMainHandItem(attacker);

        for (ItemAttribute attribute : itemItemAttributeMap.keySet())
            attribute.onPvP(attacker, victim, Tier.fromInteger(itemItemAttributeMap.get(attribute)), event);

        callOnDamaged(victim, event, null);
    }

    @Override
    protected void onPvE(Player attacker, LivingEntity victim, EntityDamageByEntityEvent event) {
        Map<ItemAttribute, Integer> itemItemAttributeMap = ItemAttribute.getAttributesOnMainHandItem(attacker);

        for (ItemAttribute attribute : itemItemAttributeMap.keySet())
            attribute.onPvE(attacker, victim, Tier.fromInteger(itemItemAttributeMap.get(attribute)), event);
    }

    @Override
    protected void onInteract(Player player, PlayerInteractEvent event) {
        Map<ItemAttribute, Integer> itemItemAttributeMap = ItemAttribute.getAttributesOnMainHandItem(player);

        for (ItemAttribute attribute : itemItemAttributeMap.keySet())
            attribute.onClick(player, Tier.fromInteger(itemItemAttributeMap.get(attribute)), event);
    }

    /**
     * Looks for a piece of armor that is enchanted
     * and calls its respective damage method,
     * null the event that is not being used
     */
    private void callOnDamaged(Player victim, @Nullable EntityDamageByEntityEvent attackedEvent, @Nullable EntityDamageEvent damageEvent) {
        //calling the victims enchantable armor piece for being attacked
        ItemStack victimsArmor = null;
        for (ItemStack item : victim.getInventory().getArmorContents())
            if (TBSItemUtil.isEnchantable(item))
                victimsArmor = item;

        if (victimsArmor != null) {
            EnchantableItem victimsEnchantableArmor = EnchantableItem.fromItemStack(victimsArmor);
            if (victimsEnchantableArmor != null)
                for (ItemAttribute attribute : victimsEnchantableArmor.getAttributeTierMap().keySet())
                    attribute.onDamaged(victim, Tier.fromInteger(victimsEnchantableArmor.getAttributeTierMap().get(attribute))
                            , attackedEvent == null ? damageEvent : attackedEvent);
        }
    }


    @Override
    protected DungeonGame getGame() {
        return (DungeonGame) game;
    }
}
