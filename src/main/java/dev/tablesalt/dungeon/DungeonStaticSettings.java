package dev.tablesalt.dungeon;

import org.mineacademy.fo.model.SimpleTime;
import org.mineacademy.fo.settings.SimpleSettings;

public class DungeonStaticSettings extends SimpleSettings {

    public static class Loot {
        public static Integer MONEY_PER_NUGGET;

        private static void init() {
            setPathPrefix("loot");

            MONEY_PER_NUGGET = getInteger("money_per_nugget", 10);
        }
    }

    public static class GameConfig {
        public static SimpleTime TIME_UNTIL_STOP;

        public static SimpleTime TIME_BETWEEN_GAMES;

        private static void init() {
            setPathPrefix("game");

            TIME_UNTIL_STOP = getTime("time_until_stop");
            TIME_BETWEEN_GAMES = getTime("time_between_games");

            if (TIME_UNTIL_STOP == null)
                TIME_UNTIL_STOP = SimpleTime.from("10 minutes");

            if (TIME_BETWEEN_GAMES == null)
                TIME_BETWEEN_GAMES = SimpleTime.from("5 seconds");
        }
    }

    public static class Database {

        public static String HOST;

        public static Integer PORT;

        public static String DATABASE_NAME;

        public static String USERNAME;

        public static String PASSWORD;

        private static void init() {
            setPathPrefix("database");

            HOST = getString("host");
            PORT = getInteger("port");
            DATABASE_NAME = getString("database");
            USERNAME = getString("user");
            PASSWORD = getString("password");
        }
    }


}
