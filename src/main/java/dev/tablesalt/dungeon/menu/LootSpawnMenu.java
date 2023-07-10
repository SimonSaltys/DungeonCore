package dev.tablesalt.dungeon.menu;

import dev.tablesalt.dungeon.maps.spawnpoints.LootPoint;
import dev.tablesalt.dungeon.util.PlayerUtil;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.button.ButtonMenu;
import org.mineacademy.fo.menu.model.MenuClickLocation;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.Arrays;
import java.util.Stack;

public class LootSpawnMenu extends Menu {

    private final LootPoint point;

    private final Button lootConfigButton;



    private LootSpawnMenu(LootPoint point) {
        this.point = point;
        setTitle("&0&lMonster Point Menu");
        setSize(9);

        lootConfigButton = makeLootConfigButton();
    }

    public static void openConfigMenu(Player player, LootPoint point) {
        new LootSpawnMenu(point).displayTo(player);
    }

    @Override
    public ItemStack getItemAt(int slot) {

        if (slot == getCenterSlot())
            return lootConfigButton.getItem();

        return NO_ITEM;
    }

    @Override
    public Menu newInstance() {
        return new LootSpawnMenu(point);
    }


    private ButtonMenu makeLootConfigButton() {
        return new ButtonMenu(new LootConfigMenu(point), CompMaterial.CHEST,"&3&lLoot Config");
    }
    private static final class LootConfigMenu extends Menu {

        private final LootPoint point;

        private final Stack<ItemStack> loot;
        private LootConfigMenu(LootPoint point) {

            this.point = point;
            setTitle("&0&lLoot Config Menu");
            setSize(18);
            loot = new Stack<>();
           loot.addAll(point.getLoot());
        }

        @Override
        public ItemStack getItemAt(int slot) {
            for (int i = 0; i < getSize(); i++)
                if (i == slot && !loot.isEmpty())
                    return loot.pop();

            return NO_ITEM;
        }

        @Override
        protected boolean isActionAllowed(MenuClickLocation location, int slot, @Nullable ItemStack clicked, @Nullable ItemStack cursor) {
            return true;
        }

        @Override
        protected void onMenuClose(Player player, Inventory inventory) {
           point.setLoot(Arrays.stream(inventory.getContents()).toList());
           PlayerUtil.getMapSafe(player).save();
        }
    }
}
