package dev.tablesalt.dungeon.item.impl.weapon;

import dev.tablesalt.dungeon.database.EnchantableItem;
import dev.tablesalt.dungeon.item.ItemAttribute;
import dev.tablesalt.dungeon.item.ItemType;
import dev.tablesalt.dungeon.item.Rarity;
import dev.tablesalt.dungeon.item.Tier;
import dev.tablesalt.dungeon.nms.HealthPackets;
import dev.tablesalt.dungeon.util.TBSItemUtil;
import lombok.Getter;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

public class VampireAttribute extends ItemAttribute {

    @Getter
    private static final VampireAttribute instance = new VampireAttribute();

    @Getter
    private final Rarity rarity = Rarity.RARE;

    private VampireAttribute() {
    }

    @Override
    public String getName() {
        return "&cVampire";
    }

    @Override
    public void onPvP(Player attacker, Player victim, EnchantableItem item, Tier tier, EntityDamageByEntityEvent event) {
        healDamageDealt(attacker, tier, event.getDamage());
    }

    @Override
    public void onPvE(Player attacker, LivingEntity victim, EnchantableItem item, Tier tier, EntityDamageByEntityEvent event) {
        healDamageDealt(attacker, tier, event.getDamage());
    }

    private void healDamageDealt(Player player, Tier tier, double damageDealt) {
        double percentageToHeal = getPercentageToHeal(tier);
        double healingAmount = damageDealt * (percentageToHeal / 100.0);

        double MAX_HEALTH = 20.0; // Maximum player health
        double currentHeath = player.getHealth();
        double healthToHeal = Math.min(currentHeath + healingAmount, MAX_HEALTH);


        player.setHealth(healthToHeal);
        HealthPackets.sendRegenPacket(player, healthToHeal);
    }

    @Override
    public List<String> getAttributeLore(Tier tier) {
        return List.of(new String[]{" ",
                TBSItemUtil.makeItemTitle(getName() + " " + tier.getAsRomanNumeral()),
                rarity.getFormattedName() + "&7When dealing damage heal for",
                "&7&b" + getPercentageToHeal(tier) + "% &7of the damage dealt",
                " "
        });
    }


    private int getPercentageToHeal(Tier tier) {
        if (tier == Tier.ONE)
            return 20;

        if (tier == Tier.TWO)
            return 30;

        if (tier == Tier.THREE)
            return 40;

        return 10;
    }

    @Override
    public ItemType getType() {
        return ItemType.WEAPON;
    }
}
