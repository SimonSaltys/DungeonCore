package dev.tablesalt.dungeon.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MessageUtil {

    public String makeInfo(String message) {
        return "&e&lINFO! &r" + message;
    }


    public String getPromptPrefix() {
        return "&c&lPROMPT! &r";
    }




}
