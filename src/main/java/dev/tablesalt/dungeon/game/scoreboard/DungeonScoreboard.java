package dev.tablesalt.dungeon.game.scoreboard;

import dev.tablesalt.dungeon.database.DungeonCache;
import dev.tablesalt.dungeon.game.DungeonGame;
import dev.tablesalt.dungeon.maps.DungeonMap;
import dev.tablesalt.gamelib.game.helpers.GameScoreboard;
import dev.tablesalt.gamelib.game.utils.GameUtil;
import org.bukkit.entity.Player;
import org.mineacademy.fo.model.Replacer;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class DungeonScoreboard extends GameScoreboard {
    private final DungeonGame game;

    private final NumberFormat formatter = new DecimalFormat("#0.00");

    public DungeonScoreboard(DungeonGame game) {
        super(game);

        this.game = game;
    }

    @Override
    protected String replaceVariablesLate(Player player, String message) {
        DungeonMap map = game.getMapRotator().getCurrentMap();
        DungeonCache cache = DungeonCache.from(player);


        if (map == null)
            return "";

        return Replacer.replaceArray(message,
                "loot_spawns", GameUtil.generateColoredGradientNumerical(map.getMaxLootSpawns(), map.getLootPoints().size()),
                "monster_spawns", GameUtil.generateColoredGradientNumerical(map.getMaxMonstersSpawns(), map.getMonsterPoints().size()),
                "player_spawns", GameUtil.generateColoredGradientNumerical(game.getMaxPlayers(), map.getPlayerSpawnPoints().size()),
                "extract_regions", GameUtil.generateColoredGradientNumerical(map.getExtractRegionAmount(), map.getExtractRegions().size()),
                "current_map", map.getName(),
                "money", formatter.format(cache.getMoney()),
                "combat_status", cache.isInCombat());
    }

    @Override
    public void addEditRows() {
        addRows("&cCurrent Map: &a{current_map}",
                "&cLoot Points: {loot_spawns}",
                "&cMonsters Points: {monster_spawns}",
                "&cPlayer Points {player_spawns}",
                "&cExtract Regions {extract_regions}");
    }

    @Override
    public void addStartRows() {
        super.addStartRows();

        addRows("",
                "Gold: &6{money}g",
                "Combat: {combat_status}");
    }

    @Override
    protected boolean showTimeLeft() {
        return true;
    }
}
