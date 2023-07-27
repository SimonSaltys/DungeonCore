package dev.tablesalt.dungeon.commands;

import dev.tablesalt.dungeon.collection.RandomCollection;
import dev.tablesalt.dungeon.item.Rarity;
import dev.tablesalt.dungeon.maps.spawnpoints.LootPoint;
import dev.tablesalt.dungeon.menu.impl.EnchantingMenu;
import dev.tablesalt.dungeon.menu.impl.LootChanceMenu;
import dev.tablesalt.gamelib.commands.GameSubCommand;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;
import org.mineacademy.fo.Common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class GetEnchantableCommand extends GameSubCommand {
    private GetEnchantableCommand() {
        super("enchantable", 0, "", "adds attribute to held item");
    }

    @Override
    protected void onCommand() {



//        final List<Pair<LootPoint.DropType, Double>> itemWeights = new ArrayList<>();
//
//        itemWeights.add(new Pair<>(LootPoint.DropType.NO_ITEM, 0.70));
//        itemWeights.add(new Pair<>(LootPoint.DropType.MYSTIC, 0.005));
//        itemWeights.add(new Pair<>(LootPoint.DropType.GOLD, 0.292));
//
//        EnumeratedDistribution<LootPoint.DropType> distribution = new EnumeratedDistribution<>(itemWeights);
//
//
//        int none = 0;
//        int myst = 0;
//        int gold = 0;
//       for (int i = 0; i < 240; i++) {
//           LootPoint.DropType dropType =  distribution.sample();
//
//           if (dropType.equals(LootPoint.DropType.NO_ITEM))
//               none++;
//
//           if (dropType.equals(LootPoint.DropType.MYSTIC))
//               myst++;
//
//           if (dropType.equals(LootPoint.DropType.GOLD))
//               gold++;
//
//       }
////
//        Common.broadcast("Rolled &7" + none + "&r None &b" + myst + "&r Myst &e" + gold + "&r Gold");

//        EnchantingMenu.openEnchantMenu(getPlayer());
//        int common = 0;
//        int rare = 0;
//        int epic = 0;
//        int mythic = 0;
//       for (int i = 0; i < 30; i++) {
//           Rarity rarity = Rarity.getRandomWeighted();
//
//           if (rarity.equals(Rarity.COMMON))
//               common++;
//
//           if (rarity.equals(Rarity.RARE))
//               rare++;
//
//           if (rarity.equals(Rarity.EPIC))
//               epic++;
//
//           if (rarity.equals(Rarity.MYTHIC))
//               mythic++;
//       }
//
//       Common.broadcast("Rolled "
//               + common + " " + Rarity.COMMON.getFormattedName()
//               +  "&d " + rare + " " + Rarity.RARE.getFormattedName()
//               +  " &b" + epic + " " + Rarity.EPIC.getFormattedName()
//               +" &e" + mythic + " " + Rarity.MYTHIC.getFormattedName());


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
