package dev.tablesalt.dungeon.commands;

import dev.tablesalt.dungeon.item.impl.TestAttribute;
import dev.tablesalt.gamelib.commands.GameSubCommand;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;

import java.util.List;

public final class ApplyAttributeCommand extends GameSubCommand {
    private ApplyAttributeCommand() {
        super("apply", 1, "<type>", "adds attribute to held item");
    }

    @Override
    protected void onCommand() {
        String name = this.joinArgs(1);
        Player player = getPlayer();

        ItemStack itemStack = player.getInventory().getItemInMainHand();
        itemStack = TestAttribute.getInstance().equipTagTo(itemStack);
        player.getInventory().setItemInMainHand(itemStack);

        Common.broadcast(TestAttribute.getInstance().hasAttribute(itemStack) + "");
    }

    @Override
    protected List<String> tabComplete() {
        return this.args.length == 1 ? this.completeLastWord(TestAttribute.getInstance().getTag()) : NO_COMPLETE;
    }
}
