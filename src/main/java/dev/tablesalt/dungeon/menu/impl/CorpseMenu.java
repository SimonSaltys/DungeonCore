package dev.tablesalt.dungeon.menu.impl;

import dev.tablesalt.dungeon.menu.TBSMenu;
import dev.tablesalt.dungeon.nms.PlayerCorpse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.model.InventoryDrawer;

public class CorpseMenu extends TBSMenu {

    private final PlayerCorpse corpse;

    public static void openCorpseMenu(Player displayTo, PlayerCorpse corpse) {
        new CorpseMenu(corpse).displayTo(displayTo);
    }

    private CorpseMenu(PlayerCorpse corpse) {
        setSize(9 * 5);
        this.corpse = corpse;
    }

    @Override
    protected void onDisplay(InventoryDrawer drawer) {
        ItemStack[] corpseItems = corpse.getItems();
        ItemStack[] armorContents = corpse.getArmor();
        ItemStack offHand = corpse.getOffHand();

        int indexInMenu = 0;
        for (int i = armorContents.length - 1; i >= 0; i--) {
            drawer.setItem(indexInMenu, armorContents[i]);
            indexInMenu++;
        }

        drawer.setItem(indexInMenu, offHand);

        int indexInCorpseItems = 0;
        for (int i = drawer.getSize() - 1; i >= 0 && indexInCorpseItems < corpseItems.length; i--) {
            drawer.setItem(i, corpseItems[indexInCorpseItems]);
            indexInCorpseItems++;
        }
    }


    @Override
    public Menu newInstance() {
        return super.newInstance();
    }
}
