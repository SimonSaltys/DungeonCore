package dev.tablesalt.dungeon.item.impl.armor;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import dev.tablesalt.dungeon.database.DungeonCache;
import dev.tablesalt.dungeon.item.ItemAttribute;
import dev.tablesalt.dungeon.item.Rarity;
import dev.tablesalt.dungeon.item.Tier;
import dev.tablesalt.dungeon.util.TBSItemUtil;
import dev.tablesalt.gamelib.game.utils.SimpleRunnable;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.List;

public final class LightWeightAttribute extends ItemAttribute {

    @Getter
    private static final LightWeightAttribute instance = new LightWeightAttribute();

    private static final HashMap<DungeonCache, Tier> playersWithAttribute = new HashMap<>();

    @Getter
    private final Rarity rarity;

    private final SimpleRunnable giveSpeedRunnable;

    private LightWeightAttribute() {
        super();

        this.rarity = Rarity.RARE;
        this.giveSpeedRunnable = new LightWeightRunnable();
        giveSpeedRunnable.launch();
    }

    @Override
    public void onArmorEquip(Player player, Tier tier, PlayerArmorChangeEvent event) {
        DungeonCache cache = DungeonCache.from(player);

        if (!playersWithAttribute.containsKey(cache))
            playersWithAttribute.put(cache, tier);

    }

    @Override
    public void onArmorTakeOff(Player player, Tier tier, PlayerArmorChangeEvent event) {
        playersWithAttribute.remove(DungeonCache.from(player));
        player.removePotionEffect(PotionEffectType.SPEED);


    }

    @Override
    public String getName() {
        return "&bLightweight";
    }

    @Override
    public List<String> getAttributeLore(Tier tier) {
        return List.of(new String[]{" ",
                TBSItemUtil.makeItemTitle(getName() + " " + tier.getAsRomanNumeral()),
                rarity.getFormattedName() + "&7You have speed &b" + tier.getAsInteger(),
                "when out of combat",
                " "
        });
    }

    @Override
    public boolean isForArmor() {
        return true;
    }

    private static class LightWeightRunnable extends SimpleRunnable {

        protected LightWeightRunnable() {
            super(-1, 0, 10);
        }

        @Override
        protected void onTick() {
            for (DungeonCache cache : playersWithAttribute.keySet())
                if (!cache.isInCombat())
                    cache.toPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100,
                            playersWithAttribute.get(cache).getAsInteger(), false));
                else
                    cache.toPlayer().removePotionEffect(PotionEffectType.SPEED);
        }

        @Override
        protected void onEnd() {
            playersWithAttribute.clear();
        }

        @Override
        protected void onTickError(Throwable t) {
            playersWithAttribute.clear();
        }
    }
}
