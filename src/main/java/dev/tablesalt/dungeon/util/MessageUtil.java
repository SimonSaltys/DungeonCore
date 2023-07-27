package dev.tablesalt.dungeon.util;

import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.mineacademy.fo.remain.Remain;

@UtilityClass
public class MessageUtil {

    public String makeInfo(String message) {
        return "&e&lINFO! &r" + message;
    }

    public String makeError(String message) { return "&c&lERROR! &r" + message;}

    public String makeSuccessful(String message) {return "&a&lSuccess! &r" + message; }

    public String makeClickable(String message, ClickEvent event) {
        TextComponent textComponent = new TextComponent(message);

        textComponent.setColor(ChatColor.GOLD);
        textComponent.setBold(true);
        textComponent.setClickEvent(event);

        return textComponent.getText();
    }




    public String getPromptPrefix() {
        return "&c&lPROMPT! &r";
    }

    public void clearTitle(Player player) {
        Remain.sendTitle(player,"","");
    }





}
