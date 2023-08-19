package dev.tablesalt.dungeon.item.impl.armor;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import dev.tablesalt.dungeon.item.ItemAttribute;
import dev.tablesalt.dungeon.item.Rarity;
import dev.tablesalt.dungeon.item.Tier;
import dev.tablesalt.dungeon.util.TBSItemUtil;
import dev.tablesalt.gamelib.game.utils.SimpleRunnable;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class LightWeightAttribute extends ItemAttribute {

    @Getter
    private static final LightWeightAttribute instance = new LightWeightAttribute();

    @Getter
    private final Rarity rarity;

    private final List<Player> playersWithAttribute;

    private final SimpleRunnable giveSpeedRunnable;

    private LightWeightAttribute() {
        super();

        this.rarity = Rarity.RARE;
        playersWithAttribute = new ArrayList<>();
        this.giveSpeedRunnable = new LightWeightRunnable();
    }

    @Override
    public void onArmorEquip(Player player, PlayerArmorChangeEvent event) {
        if (!playersWithAttribute.contains(player))
            playersWithAttribute.add(player);
    }

    @Override
    public void onArmorTakeOff(Player player, PlayerArmorChangeEvent event) {
        playersWithAttribute.remove(player);
    }

    private static class LightWeightRunnable extends SimpleRunnable {

        protected LightWeightRunnable() {
            super(-1, 0, 10);
        }

        @Override
        protected void onTick() {

        }

        @Override
        protected void onEnd() {

        }
    }


    @Override
    public String getName() {
        return "&bLightweight";
    }

    @Override
    public List<String> getAttributeLore(Tier tier) {
        return List.of(new String[]{" ",
                TBSItemUtil.makeItemTitle(getName() + " " + tier.getAsRomanNumeral()),
                rarity.getFormattedName() + "&7you move fast idk",
                " "
        });
    }

    @Override
    public boolean isForArmor() {
        return true;
    }
}
