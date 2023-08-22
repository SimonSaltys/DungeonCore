package dev.tablesalt.dungeon.item.impl.weapon;

import dev.tablesalt.dungeon.item.ItemAttribute;
import dev.tablesalt.dungeon.item.Rarity;
import dev.tablesalt.dungeon.item.Tier;
import dev.tablesalt.dungeon.model.Cooldown;
import dev.tablesalt.dungeon.util.TBSItemUtil;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.model.SimpleTime;

import java.util.List;

public final class FireBallAttribute extends ItemAttribute {

    @Getter
    private static final FireBallAttribute instance = new FireBallAttribute();

    private final Cooldown cooldown = new Cooldown();

    @Getter
    private final Rarity rarity;

    private FireBallAttribute() {
        rarity = Rarity.EPIC;
    }

    @Override
    public String getName() {
        return "&cFireball";
    }

    @Override
    public void onClick(Player clicker, Tier tier, PlayerInteractEvent event) {
        if (cooldown.hasTimeLeft(clicker)) {
            Common.broadcast("Seconds left: " + cooldown.getSecondsLeft(clicker));
            return;
        }

        Common.broadcast("&eShot &6Fireball!");

        cooldown.startCooldown(clicker, SimpleTime.fromSeconds(getRechargeTime(tier)));
    }

    @Override
    public List<String> getAttributeLore(Tier tier) {
        return List.of(new String[]{" ",
                TBSItemUtil.makeItemTitle(getName() + " " + tier.getAsRomanNumeral()),
                rarity.getFormattedName() + "&7On &6attack&7, shoots a fireball",
                "&7that recharges every " + getRechargeTime(tier) + " seconds",
                " "
        });
    }

    private int getRechargeTime(Tier tier) {
        if (tier == Tier.ONE)
            return 15;

        if (tier == Tier.TWO)
            return 10;

        if (tier == Tier.THREE)
            return 5;

        return 20;
    }

    @Override
    public boolean isForArmor() {
        return false;
    }
}
