package dev.tablesalt.dungeon.menu.impl;

import dev.tablesalt.dungeon.configitems.LootChance;
import dev.tablesalt.dungeon.maps.spawnpoints.LootPoint;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.MenuPagged;
import org.mineacademy.fo.menu.button.ButtonMenu;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

public class LootSpawnMenu extends Menu {

    private final LootPoint point;

    private final ButtonMenu selectChance;


    private LootSpawnMenu(LootPoint point) {
        this.point = point;
        setTitle("&eLoot Point Menu");
        setSize(9);

        selectChance = new ButtonMenu(new SelectChanceMenu(point), CompMaterial.BOOKSHELF, "Select Chance Type","");

    }

    public static void openConfigMenu(Player player, LootPoint point) {
        new LootSpawnMenu(point).displayTo(player);
    }

    @Override
    public ItemStack getItemAt(int slot) {

        if (slot == getCenterSlot())
            return selectChance.getItem();

        return NO_ITEM;
    }

    @Override
    public Menu newInstance() {
        return new LootSpawnMenu(point);
    }


    private static class SelectChanceMenu extends MenuPagged<LootChance> {

        final LootPoint lootPoint;

        public SelectChanceMenu(LootPoint lootPoint) {
            super(LootChance.getChances());

            this.lootPoint = lootPoint;
        }

        @Override
        protected ItemStack convertToItemStack(LootChance chance) {
            return ItemCreator.of(chance.convertToItem())
                    .glow(lootPoint.getLootChance() != null && lootPoint.getLootChance().getName().equals(chance.getName())).make();
        }

        @Override
        protected void onPageClick(Player player, LootChance item, ClickType click) {
            lootPoint.setLootChance(item);
            restartMenu();
        }

        @Override
        public Menu newInstance() {
            return new SelectChanceMenu(lootPoint);
        }
    }


}
