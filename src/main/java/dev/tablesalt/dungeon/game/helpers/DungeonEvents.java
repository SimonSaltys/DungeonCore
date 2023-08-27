package dev.tablesalt.dungeon.game.helpers;

import dev.tablesalt.dungeon.database.DungeonCache;
import dev.tablesalt.dungeon.database.EnchantableItem;
import dev.tablesalt.dungeon.database.Keys;
import dev.tablesalt.dungeon.game.DungeonGame;
import dev.tablesalt.dungeon.model.TBSSound;
import dev.tablesalt.dungeon.nms.PlayerCorpse;
import dev.tablesalt.dungeon.util.DungeonUtil;
import dev.tablesalt.dungeon.util.TBSItemUtil;
import dev.tablesalt.gamelib.game.helpers.GameEvents;
import org.apache.commons.math3.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
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

import java.util.HashMap;

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
    protected void onPlayerDamagedByEntity(Entity attacker, Player victim, EntityDamageByEntityEvent event) {
        startCombat(victim);
    }

    @Override
    protected void onPvP(Player attacker, Player victim, EntityDamageByEntityEvent event) {

        EnchantableItem item = EnchantableItem.fromItemStack(attacker.getInventory().getItemInMainHand());

        if (item != null)
            item.forAllAttributes((itemAttribute, tier) ->
                    itemAttribute.onPvP(attacker, victim, item, tier, event));


        callOnDamaged(victim, event, null);
        startCombat(victim);
        startCombat(attacker);
    }

    @Override
    protected void onPvE(Player attacker, LivingEntity victim, EntityDamageByEntityEvent event) {
        EnchantableItem item = EnchantableItem.fromItemStack(attacker.getInventory().getItemInMainHand());

        if (item != null)
            item.forAllAttributes((itemAttribute, tier) ->
                    itemAttribute.onPvE(attacker, victim, item, tier, event));


        startCombat(attacker);
    }

    @Override
    protected void onInteract(Player player, PlayerInteractEvent event) {
        EnchantableItem item = EnchantableItem.fromItemStack(player.getInventory().getItemInMainHand());

        if (item != null)
            item.forAllAttributes((itemAttribute, tier) ->
                    itemAttribute.onClick(player, item, tier, event));


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

                victimsEnchantableArmor.forAllAttributes((itemAttribute, tier) -> {

                    if (attackedEvent == null)
                        itemAttribute.onDamaged(victim, victimsEnchantableArmor, tier, damageEvent);
                    else
                        itemAttribute.onDamaged(victim, victimsEnchantableArmor, tier, attackedEvent);

                });


        }
    }


    private final HashMap<Player, Pair<Long, Integer>> playersInCombat = new HashMap<>();

    private void startCombat(Player player) {
        long currentTime = System.currentTimeMillis();

        updateCombatTask(player);

        int taskId;
        DungeonCache.from(player).setInCombat(true);

        taskId = Common.runLaterAsync(3 * 20, () -> {
            playersInCombat.remove(player);
            DungeonCache.from(player).setInCombat(false);


        }).getTaskId();


        playersInCombat.put(player, Pair.create(currentTime, taskId));
    }

    private void updateCombatTask(Player player) {
        if (playersInCombat.containsKey(player)) {
            int existingTaskId = playersInCombat.get(player).getSecond();
            Bukkit.getScheduler().cancelTask(existingTaskId);
        }
    }


    @Override
    protected DungeonGame getGame() {
        return (DungeonGame) game;
    }
}
