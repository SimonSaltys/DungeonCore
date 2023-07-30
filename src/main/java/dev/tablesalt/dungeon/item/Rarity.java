package dev.tablesalt.dungeon.item;

import dev.tablesalt.dungeon.collection.RandomCollection;
import dev.tablesalt.gamelib.game.utils.TBSColor;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.mineacademy.fo.ChatUtil;
import org.mineacademy.fo.ItemUtil;
import org.mineacademy.fo.remain.CompChatColor;
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum Rarity {

    COMMON("",50),

    RARE("&2RARE! ",25),

    EPIC("&c&lEPIC! ",200),
    MYTHIC("&k!&l"+ ChatUtil.generateGradient("MYTHIC", CompChatColor.YELLOW, CompChatColor.GOLD) + "&k!&r"
    ,3);

    private final String formattedName;
    private final int chanceToRoll;



    @Override
    public String toString() {
        return ItemUtil.bountifyCapitalized(name());
    }

    public static Rarity getRandomWeighted() {
        return getRandomWeighted(null);
    }

    public static Rarity getRandomWeighted(Rarity excluded) {

        RandomCollection<Rarity> randomCollection = new RandomCollection<>();

        for (Rarity rarity : Rarity.values()) {
            if (rarity.equals(excluded))
                continue;

            randomCollection.add(rarity.getChanceToRoll(),rarity);
        }

        return randomCollection.next();
    }
}
