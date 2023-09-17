package dev.tablesalt.dungeon.bungee;

import dev.tablesalt.dungeon.database.Keys;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.bungee.BungeeListener;
import org.mineacademy.fo.bungee.message.IncomingMessage;

@AutoRegister
public final class DungeonBungeeListener extends BungeeListener {

    @Getter
    private static final DungeonBungeeListener instance = new DungeonBungeeListener();


    private DungeonBungeeListener() {
        super(Keys.BUNGEE_MESSAGE_CHANNEL, DungeonMessage.class);
    }

    @Override
    public void onMessageReceived(Player player, IncomingMessage message) {
    }



    private void handleServerDataMessage(Player player, IncomingMessage message) {
    }
}
