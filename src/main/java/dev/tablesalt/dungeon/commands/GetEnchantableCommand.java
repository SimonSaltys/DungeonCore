package dev.tablesalt.dungeon.commands;

import dev.tablesalt.dungeon.database.DungeonCache;
import dev.tablesalt.dungeon.database.EnchantableItem;
import dev.tablesalt.dungeon.item.impl.Rarity;
import dev.tablesalt.dungeon.menu.enchanting.EnchantingMenu;
import dev.tablesalt.gamelib.commands.GameSubCommand;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.annotation.AutoRegister;

public final class GetEnchantableCommand extends GameSubCommand {
    private GetEnchantableCommand() {
        super("enchantable", 0, "", "adds attribute to held item");
    }

    @Override
    protected void onCommand() {

        EnchantingMenu.openEnchantMenu(getPlayer());
        int common = 0;
        int rare = 0;
        int epic = 0;
        int mythic = 0;
       for (int i = 0; i < 30; i++) {
           Rarity rarity = Rarity.getRandomWeighted();

           if (rarity.equals(Rarity.COMMON))
               common++;

           if (rarity.equals(Rarity.RARE))
               rare++;

           if (rarity.equals(Rarity.EPIC))
               epic++;

           if (rarity.equals(Rarity.MYTHIC))
               mythic++;
       }

       Common.broadcast("Rolled "
               + common + " " + Rarity.COMMON.getFormattedName()
               +  "&d " + rare + " " + Rarity.RARE.getFormattedName()
               +  " &b" + epic + " " + Rarity.EPIC.getFormattedName()
               +" &e" + mythic + " " + Rarity.MYTHIC.getFormattedName());


//       ItemStack item = getPlayer().getInventory().getItemInMainHand();
//
//       EnchantableItem enchantableItem = EnchantableItem.fromItemStack(item);
//
//       if (enchantableItem != null)
//           tellSuccess("Valid! " + enchantableItem);
//       else
//           tellError("Invalid.");

    }
}
