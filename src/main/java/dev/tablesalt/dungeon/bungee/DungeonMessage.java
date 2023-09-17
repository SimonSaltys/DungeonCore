package dev.tablesalt.dungeon.bungee;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.mineacademy.fo.bungee.BungeeMessageType;

public enum DungeonMessage implements BungeeMessageType {

    SERVER_DATA( /*server name*/ String.class, /*players online*/ Integer.class);

    private final Class<?>[] content;

    DungeonMessage(Class<?>... content) {
        this.content = content;
    }

    @Override
    public Class<?>[] getContent() {
        return new Class[0];
    }
}
