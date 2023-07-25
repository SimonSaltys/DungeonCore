package dev.tablesalt.dungeon.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.model.ItemCreator;

public abstract class TBSButton extends Button {

    private ItemCreator creator;

    @Override
    public abstract void onClickedInMenu(Player player, Menu menu, ClickType click);

    public TBSButton(ItemCreator creator) {
        this.creator = creator;
    }

    public void setCreator(ItemCreator creator) {
        this.creator = creator;
    }

    public ItemCreator getCreator() {
        return creator;
    }

    @Override
    public final ItemStack getItem() {
        return creator.make();
    }
}
