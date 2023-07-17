package dev.tablesalt.dungeon.item.impl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.mineacademy.fo.Common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static Tier fromRomanNumeral(String line) {
        Pattern pattern = Pattern.compile("\\b(?=[MDCLXVI])M*D?C{0,4}L?X{0,4}V?I{0,4}\\b");
        Matcher matcher = pattern.matcher(line);

        String numeral = "";
        if (matcher.find())
            numeral = matcher.group();

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
