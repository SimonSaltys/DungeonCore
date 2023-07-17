package dev.tablesalt.dungeon.item.impl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.mineacademy.fo.Common;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum Tier {

    NONE(0,"", 2000),
    ONE(1,"I", 4000),
    TWO(2,"II", 6000),
    THREE(3,"III", 8000);

    private final int asInteger;
    private final String asRomanNumeral;
    private final int costToUpgrade;

    public static Tier fromInteger(int i) {
        for (Tier tier : Tier.values())
            if (tier.getAsInteger() == i)
                return tier;

        return NONE;
    }

    public static Tier fromRomanNumeral(String numeral) {
        for (Tier tier : Tier.values())
            if (tier.getAsRomanNumeral().equals(numeral))
                return tier;

        return NONE;
    }

    public static Tier getNext(Tier tier) {
        Tier nextTier = Common.getNext(tier,Tier.values(),true);

        if (nextTier == NONE)
            nextTier = THREE;

        return nextTier;
    }

}
