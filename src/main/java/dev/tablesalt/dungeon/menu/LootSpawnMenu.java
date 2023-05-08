package dev.tablesalt.dungeon.menu;

import dev.tablesalt.dungeon.game.DungeonGame;
import dev.tablesalt.dungeon.maps.DungeonMap;
import dev.tablesalt.dungeon.maps.LootSpawnPoint;
import dev.tablesalt.dungeon.util.PlayerUtil;
import dev.tablesalt.gameLib.lib.Common;
import dev.tablesalt.gameLib.lib.menu.Menu;
import dev.tablesalt.gameLib.lib.menu.button.Button;
import dev.tablesalt.gameLib.lib.menu.button.ButtonMenu;
import dev.tablesalt.gameLib.lib.menu.button.ButtonRemove;
import dev.tablesalt.gameLib.lib.menu.model.ItemCreator;
import dev.tablesalt.gameLib.lib.menu.model.MenuClickLocation;
import dev.tablesalt.gameLib.lib.remain.CompMaterial;
import dev.tablesalt.gamelib.game.helpers.Game;
import dev.tablesalt.gamelib.game.utils.GameUtil;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;

public class LootSpawnMenu extends Menu {

    private final LootSpawnPoint point;

    private final Button lootConfigButton;



    private LootSpawnMenu(LootSpawnPoint point) {
        this.point = point;
        setTitle("&0&lMonster Point Menu");
        setSize(9);

        lootConfigButton = makeLootConfigButton();
    }

    public static void openConfigMenu(Player player, LootSpawnPoint point) {
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

        private final LootSpawnPoint point;

        private final Stack<ItemStack> loot;
        private LootConfigMenu(LootSpawnPoint point) {

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
