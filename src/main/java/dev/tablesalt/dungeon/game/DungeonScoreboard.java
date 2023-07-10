package dev.tablesalt.dungeon.game;

import dev.tablesalt.dungeon.maps.DungeonMap;
import dev.tablesalt.gamelib.game.helpers.GameScoreboard;
import dev.tablesalt.gamelib.game.utils.GameUtil;
import org.bukkit.entity.Player;
import org.mineacademy.fo.model.Replacer;

public class DungeonScoreboard extends GameScoreboard {
    private final DungeonGame game;
    public DungeonScoreboard(DungeonGame game) {
        super(game);

        this.game = game;
    }

    @Override
    protected String replaceVariablesLate(Player player, String message) {
        DungeonMap map = game.getMapRotator().getCurrentMap();
        if (map == null)
            return "";

         return Replacer.replaceArray(message,
//                "loot_spawns", GameUtil.generateColoredGradientNumerical(map.getMaxLootSpawns(),map.getLootSpawnPoints().size()),
                "monster_spawns", GameUtil.generateColoredGradientNumerical(map.getMaxMonstersSpawns(),map.getMonsterPoints().size()),
                "player_spawns", GameUtil.generateColoredGradientNumerical(game.getMaxPlayers(),map.getPlayerSpawnPoints().size()),
                "current_map", map.getName());
    }

    @Override
    public void addEditRows() {


       addRows("&cCurrent Map: &a{current_map}",
               "&cLoot Points: {loot_spawns}",
               "&cMonsters Points: {monster_spawns}",
               "&cPlayer Points {player_spawns}");

    }
}
