package dev.tablesalt.dungeon.commands;

import lombok.Getter;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.SimpleCommand;
@AutoRegister
public final class DeveloperCommand extends SimpleCommand {

    @Getter
    private static final DeveloperCommand instance = new DeveloperCommand();
    private DeveloperCommand() {
        super("dungeondev");
    }

    @Override
    protected void onCommand() {
        Common.log("Hello, world!");
    }
}
