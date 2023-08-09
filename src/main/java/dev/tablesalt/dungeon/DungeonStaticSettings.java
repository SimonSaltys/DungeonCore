package dev.tablesalt.dungeon;

import org.mineacademy.fo.model.SimpleTime;
import org.mineacademy.fo.settings.SimpleSettings;

public class DungeonStaticSettings extends SimpleSettings {

    public static class Loot {
        public static Integer moneyPerNugget;

        private static void init() {
            setPathPrefix("loot");

            moneyPerNugget = getInteger("money_per_nugget", 10);
        }
    }

    public static class GameConfig {
        public static SimpleTime timeUntilStop;

        private static void init() {
            setPathPrefix("game");

            timeUntilStop = getTime("time_until_stop");
            if (timeUntilStop == null)
                timeUntilStop = SimpleTime.from("10 minutes");
        }
    }

    public static class Database {

        public static String host;

        public static Integer port;

        public static String databaseName;

        public static String username;

        public static String password;

        private static void init() {
            setPathPrefix("database");

            host = getString("host");
            port = getInteger("port");
            databaseName = getString("database");
            username = getString("user");
            password = getString("password");
        }
    }


}
