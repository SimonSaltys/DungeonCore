package dev.tablesalt.dungeon.item.impl.weapon;

import dev.tablesalt.dungeon.DungeonPlugin;
import dev.tablesalt.dungeon.database.EnchantableItem;
import dev.tablesalt.dungeon.item.ItemAttribute;
import dev.tablesalt.dungeon.item.Rarity;
import dev.tablesalt.dungeon.item.Tier;
import dev.tablesalt.dungeon.model.TBSSound;
import dev.tablesalt.dungeon.util.TBSItemUtil;
import dev.tablesalt.gamelib.game.utils.MessageUtil;
import lombok.Getter;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.RandomUtil;

import java.util.ArrayList;
import java.util.List;

public final class CursedRetaliationAttribute extends ItemAttribute {
    @Getter
    private static final CursedRetaliationAttribute instance = new CursedRetaliationAttribute();

    private final List<LivingEntity> cursedEntities = new ArrayList<>();

    @Getter
    private final Rarity rarity;

    private CursedRetaliationAttribute() {
        super();

        this.rarity = Rarity.EPIC;
    }

    @Override
    public void onPvP(Player attacker, Player victim, Tier tier, EntityDamageByEntityEvent event) {
        if (event.getDamage() >= victim.getHealth()) {
            cursedEntities.remove(victim);
            return;
        }


        attemptToCurse(attacker, victim, tier, event);
    }

    @Override
    public void onPvE(Player attacker, LivingEntity victim, Tier tier, EntityDamageByEntityEvent event) {
        if (event.getDamage() >= victim.getHealth()) {
            cursedEntities.remove(victim);
            return;
        }

        attemptToCurse(attacker, victim, tier, event);
    }

    private void attemptToCurse(Player attacker, LivingEntity entity, Tier tier, EntityDamageByEntityEvent event) {
        boolean isChance = RandomUtil.chance(getChance(tier));
        EnchantableItem item = EnchantableItem.fromItemStack(attacker.getInventory().getItemInMainHand());

        //should never happen
        if (item == null)
            return;


        if (!isChance)
            return;

        if (cursedEntities.contains(entity))
            return;
        else
            cursedEntities.add(entity);

        double damage = getDamage(tier);
        double damageIncrement = 0.5;

        if (entity instanceof Player victim) {
            Common.tellNoPrefix(victim, MessageUtil.makeScary("You have been cursed by " + attacker.getName()));
        }

        new BukkitRunnable() {
            double totalDamage = 0;

            @Override
            public void run() {
                if (totalDamage >= damage || !cursedEntities.contains(entity) || damageIncrement >= entity.getHealth()) {
                    cursedEntities.remove(entity);
                    cancel();
                    return;
                }

                //trigger other damage events for other attributes
                if (entity instanceof Player victim) {
                    TBSSound.Debuffed.getInstance().playTo(victim);

                    for (ItemAttribute attribute : item.getAttributeTierMap().keySet())
                        attribute.onPvP(attacker, victim, tier, event);

                } else {

                    for (ItemAttribute attribute : item.getAttributeTierMap().keySet())
                        attribute.onPvE(attacker, entity, tier, event);
                }


                entity.damage(damageIncrement);
                totalDamage += damageIncrement;
            }

        }.runTaskTimer(DungeonPlugin.getInstance(), 0, 20);


    }

    @Override
    public String getName() {
        return "&cCursed Retaliation";
    }

    @Override
    public List<String> getAttributeLore(Tier tier) {
        return List.of(new String[]{" ",
                TBSItemUtil.makeItemTitle(getName() + " " + tier.getAsRomanNumeral()),
                rarity.getFormattedName() + "&7When &6striking&7 an enemy",
                "&7there is a " + getChance(tier) + "% chance to apply a curse",
                "&7that deals " + getDamage(tier) + " true damage overtime"
        });
    }

    public double getDamage(Tier tier) {
        if (tier == Tier.ONE)
            return 2.5;

        if (tier == Tier.TWO)
            return 3;

        if (tier == Tier.THREE)
            return 3.5;

        return 2;
    }

    public int getChance(Tier tier) {
        if (tier == Tier.ONE)
            return 5;

        if (tier == Tier.TWO)
            return 10;

        if (tier == Tier.THREE)
            return 20;

        return 3;

    }

    @Override
    public boolean isForArmor() {
        return false;
    }


    //might be to op so finish this class to limit the amount a player can curse each other
//    private static class CursedData {
//
//       private static final List<Triple<Player, Player,Integer>> playersCursed = new ArrayList<>();
//
//
//       public int getAmountCursedFrom(Player attacker, Player victim) {
//           for (Triple<Player,Player,Integer> cursedBy : playersCursed)
//               if (cursedBy.getLeft().equals(attacker) && cursedBy.getMiddle().equals(victim))
//                   return cursedBy.getRight();
//
//           return 0;
//       }
//
//       public void
//
//
//
//
//
//
//
//
//    }

}
