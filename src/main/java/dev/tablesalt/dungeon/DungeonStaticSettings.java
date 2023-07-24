package dev.tablesalt.dungeon;

import org.mineacademy.fo.settings.SimpleSettings;

public class DungeonStaticSettings extends SimpleSettings {

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
