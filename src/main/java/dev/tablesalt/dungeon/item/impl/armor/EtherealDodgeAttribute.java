package dev.tablesalt.dungeon.item.impl.armor;

import dev.tablesalt.dungeon.collection.Cooldown;
import dev.tablesalt.dungeon.database.EnchantableItem;
import dev.tablesalt.dungeon.database.Keys;
import dev.tablesalt.dungeon.item.EnchantmentLifecycle;
import dev.tablesalt.dungeon.item.ItemAttribute;
import dev.tablesalt.dungeon.item.Rarity;
import dev.tablesalt.dungeon.item.Tier;
import dev.tablesalt.dungeon.model.TBSSound;
import dev.tablesalt.dungeon.model.effects.Trail;
import dev.tablesalt.dungeon.util.TBSItemUtil;
import dev.tablesalt.gamelib.players.PlayerCache;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.jetbrains.annotations.Nullable;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.model.SimpleTime;

import java.util.List;

public class EtherealDodgeAttribute extends ItemAttribute implements EnchantmentLifecycle {

    @Getter
    private static final EtherealDodgeAttribute instance = new EtherealDodgeAttribute();

    private static final Cooldown dodgeCooldown = new Cooldown();


    @Getter
    private final Rarity rarity;

    private EtherealDodgeAttribute() {
        this.rarity = Rarity.MYTHIC;
    }

    @Override
    public void start(Player player, EnchantableItem item, Tier tier) {
        player.setAllowFlight(true);
    }

    @Override
    public void stop(Player player, @Nullable EnchantableItem item, @Nullable Tier tier) {
        player.setAllowFlight(false);
        PlayerCache.from(player).getTagger().removePlayerTag(Keys.ALLOW_FLIGHT);
    }

    @Override
    public void onDamaged(Player victim, EnchantableItem item, Tier tier, EntityDamageByEntityEvent event) {
        PlayerCache cache = PlayerCache.from(victim);

        if (!dodgeCooldown.hasTimeLeft(victim)) {

            //if they take to long then don't let them dash
            Common.runLater(20 * 2, () -> {
                if (cache.getTagger().getBooleanTagSafe(Keys.ALLOW_FLIGHT)) {
                    startCooldown(victim, tier);
                    TBSSound.Debuffed.getInstance().playTo(victim);
                }

            });

            TBSSound.Buffed.getInstance().playTo(victim);
            cache.getTagger().setPlayerTag(Keys.ALLOW_FLIGHT, true);
        }
    }


    @Override
    public void onToggleFlight(Player player, EnchantableItem item, Tier tier, PlayerToggleFlightEvent event) {

        if (!PlayerCache.from(player).getTagger().getBooleanTagSafe(Keys.ALLOW_FLIGHT))
            return;

        TBSSound.playTo(TBSSound.Poof.getInstance(), player.getLocation(), 5);

        Location location = player.getLocation();

        player.setVelocity(location.getDirection().multiply(2).setY(1));
        new Trail(player).start();

        startCooldown(player, tier);
    }

    private void startCooldown(Player player, Tier tier) {
        PlayerCache.from(player).getTagger().setPlayerTag(Keys.ALLOW_FLIGHT, false);
        dodgeCooldown.startCooldown(player, SimpleTime.from("10 seconds"));
    }


    @Override
    public String getName() {
        return "&eEthereal Dodge";
    }

    @Override
    public List<String> getAttributeLore(Tier tier) {
        return List.of(new String[]{" ",
                TBSItemUtil.makeItemTitle(getName() + " " + tier.getAsRomanNumeral()),
                rarity.getFormattedName() + " &7When you are hit",
                "&7every &b" + getCooldownTime(tier) + "&7 seconds you gain",
                "&7the ability to dash for a short time",
                " "
        });
    }

    public Integer getCooldownTime(Tier tier) {
        if (tier == Tier.ONE)
            return 15;

        if (tier == Tier.TWO)
            return 10;

        if (tier == Tier.THREE)
            return 7;

        return 20;
    }

    @Override
    public boolean isForArmor() {
        return true;
    }


}
