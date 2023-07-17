package dev.tablesalt.dungeon.item.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.mineacademy.fo.ChatUtil;
import org.mineacademy.fo.remain.CompChatColor;
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum Rarity {

    COMMON("",80),
    RARE("&b&lRARE!",20),
    MYTHIC("&l"+ ChatUtil.generateGradient("MYTHIC!", CompChatColor.YELLOW, CompChatColor.GOLD)
    ,5);

    private final String formattedName;

    private final int chanceToRoll;

    //How much the enchantment accounts to the overall item
    private int weight;
}
