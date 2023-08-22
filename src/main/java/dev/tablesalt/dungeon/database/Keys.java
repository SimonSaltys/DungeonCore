package dev.tablesalt.dungeon.database;

import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.List;

@UtilityClass
public class Keys {

    public static final String DISPLAY_NAME = "Enchanted Well";

    public static final String DEAD_BODY_NAME = "Body Of ";

    public static final String IN_COMBAT = "InCombat";

    public static final int ENCHANTING_MENU_SLOT = 41;


    public static final List<String> KILL_VERB = Arrays.asList(
            "murdered", "killed", "slayed", "terminated",
            "assassinated", "annihilated", "eradicated", "liquidated",
            "eliminated", "neutralized", "dispatched", "executed",
            "strangled", "erased", "exterminated", "obliterated",
            "wiped out", "finished", "ended", "quashed",
            "smothered", "suffocated", "disposed of", "vanquished",
            "slew", "butchered", "dispatched", "destroyed",
            "eradicated", "extinguished", "removed", "snuffed out",
            "took out", "annulled", "cancelled", "erased from existence",
            "brought down", "cut down", "laid to rest", "wiped off the face of the earth",
            "made an end of", "put a stop to", "put an end to", "put down like a rabid dog",
            "put to the sword", "sent to the great beyond", "forced to retire early"
    );


}
