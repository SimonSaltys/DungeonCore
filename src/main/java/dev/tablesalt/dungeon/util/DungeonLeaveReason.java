package dev.tablesalt.dungeon.util;

import dev.tablesalt.gamelib.game.utils.Message;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum DungeonLeaveReason implements Message {

    EXTRACTED("Player Extracted"),
    DIED("Player Died");

    @Getter
    private final String message;
}
