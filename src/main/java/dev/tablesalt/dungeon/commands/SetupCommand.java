package dev.tablesalt.dungeon.commands;

import dev.tablesalt.gamelib.commands.GameSubCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

public final class SetupCommand extends GameSubCommand {
    private SetupCommand() {
        super("setup", 0, "", "gives you all the setup tools");
    }

    @Override
    protected void onCommand() {

//        EnchantingWellTool.getInstance().give(getPlayer());
        Player player = getPlayer();

        player.getInventory().setItem(EquipmentSlot.HEAD, ItemCreator.of(CompMaterial.DIAMOND_HELMET).make());
        player.getInventory().setItem(EquipmentSlot.CHEST, ItemCreator.of(CompMaterial.DIAMOND_CHESTPLATE).make());
        player.getInventory().setItem(EquipmentSlot.LEGS, ItemCreator.of(CompMaterial.DIAMOND_LEGGINGS).make());
        player.getInventory().setItem(EquipmentSlot.FEET, ItemCreator.of(CompMaterial.DIAMOND_BOOTS).make());

        player.getInventory().setItem(EquipmentSlot.OFF_HAND, ItemCreator.of(CompMaterial.SHIELD).make());

        for (int i = 0; i < 9; i++)
            player.getInventory().setItem(i, ItemCreator.of(CompMaterial.RED_WOOL).make());

    }
}
