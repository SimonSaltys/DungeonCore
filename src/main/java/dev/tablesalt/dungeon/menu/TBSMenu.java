package dev.tablesalt.dungeon.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.DragType;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.ArrayList;
import java.util.List;

public class TBSMenu extends Menu {


    protected List<Integer> slotsToPersist = new ArrayList<>();

    protected void playerSetItem(ItemStack item, int slot) {
        getInventory().setItem(slot, item);


        Common.runLater(1, () -> {
            getViewer().setItemOnCursor(ItemCreator.of(CompMaterial.AIR).make());
        });
    }


    public void preRestart() {
    }

    public void postRestart() {

    }

    protected void onMenuDrag(Player player, int slot, DragType type, ItemStack cursor) {


    }

    protected final void restart() {

        List<ItemStack> itemsToPutBack = new ArrayList<>();

        for (int i : slotsToPersist)
            itemsToPutBack.add(getInventory().getItem(i));

        preRestart();

        restartMenu();

        postRestart();

        for (int i = 0; i < slotsToPersist.size(); i++)
            getInventory().setItem(slotsToPersist.get(i), itemsToPutBack.get(i));


    }

}
