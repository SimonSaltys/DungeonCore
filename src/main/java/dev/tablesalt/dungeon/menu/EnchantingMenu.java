package dev.tablesalt.dungeon.menu;

import dev.tablesalt.gamelib.game.utils.SimpleRunnable;
import dev.tablesalt.gamelib.players.PlayerCache;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.menu.model.MenuClickLocation;
import org.mineacademy.fo.remain.CompMaterial;

import java.lang.reflect.Array;
import java.util.Arrays;

public class EnchantingMenu extends Menu {

    private final Button enchantButton;

    private final EnchantAnimation enchantAnimation;

    private static int ENCHANT_SLOT = 20;

    private EnchantingMenu() {
        setTitle("&5Eternal Well");
        setSize(9 * 5);
        this.enchantAnimation = new EnchantAnimation();

        enchantAnimation.launch();
        enchantButton = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType click) {
                PlayerCache cache = PlayerCache.from(player);


               if (!cache.getTagger().getBooleanTagSafe("upgrading")) {
                   cache.getTagger().setPlayerTag("upgrading",true);
                   enchantAnimation.launchColorAnimation(CompMaterial.RED_STAINED_GLASS_PANE);
               }

            }

            @Override
            public ItemStack getItem() {
                return ItemCreator.of(CompMaterial.ENCHANTING_TABLE,"&5Eternal Well",
                        "&7Upgrade: &eTODO", "&7Cost: &6TODO", "", "&eClick to enchant!").make();
            }
        };
    }

    @Override
    protected boolean isActionAllowed(MenuClickLocation location, int slot, @Nullable ItemStack clicked, @Nullable ItemStack cursor) {

        if (MenuClickLocation.PLAYER_INVENTORY == location)
            return true;

        return MenuClickLocation.MENU == location && slot == 20;
    }

    public static void openEnchantMenu(Player player) {
        new EnchantingMenu().displayTo(player);
    }

    @Override
    public ItemStack getItemAt(int slot) {

        if (slot == 24)
            return enchantButton.getItem();

        //makes a box 3x3 box in the menu
        if ((10 <= slot && slot <= 12) || slot == 21 || (30 >= slot && 28 <= slot) || slot == 19)
            return ItemCreator.of(CompMaterial.WHITE_STAINED_GLASS_PANE," ").make();


        return super.getItemAt(slot);
    }

    @Override
    protected void onMenuClose(Player player, Inventory inventory) {
       if (enchantAnimation.isRunning())
           enchantAnimation.cancel();
    }

    private ItemStack enchantItem(ItemStack item) {
        return ItemCreator.of(item).glow(true).make();
    }

    private boolean isEnchantable(ItemStack item) {
        return true;
    }

    private class EnchantAnimation extends SimpleRunnable {

        Integer count = 0;

        Integer[] positions = {10, 11, 12, 21, 30, 29, 28, 19};

        private boolean coloredAnimation = false;

        private CompMaterial colorOfAnimation = CompMaterial.WHITE_STAINED_GLASS_PANE;

        protected EnchantAnimation() {
            super(-1,3,4);
        }

        @Override
        protected void onTick() {

            if (!coloredAnimation) {
                setGlass(positions[count]);

                count++;

                if (count > 7)
                    count = 0;
            } else {
                count++;

                if (count % 4 == 0)
                   setAll(colorOfAnimation);
                else
                  setAll(CompMaterial.WHITE_STAINED_GLASS_PANE);

                if (count >= 20)
                   end();

            }


        }


        @Override
        protected void onEnd() {
            count = 0;

            if (coloredAnimation) {
                coloredAnimation = false;

                ItemStack item = getInventory().getItem(20);
                    setItem(ENCHANT_SLOT,enchantItem(item));
                    PlayerCache.from(getViewer()).getTagger().setPlayerTag("upgrading",false);
                    setAll(CompMaterial.WHITE_STAINED_GLASS_PANE);

                launchBasicTrail();
            }


        }


        protected void launchBasicTrail() {
            if (isRunning())
                cancel();

            launch();
        }

        protected void launchColorAnimation(CompMaterial material) {
            if (isRunning())
                cancel();

            colorOfAnimation = material;
            coloredAnimation = true;
            launch();

        }

        private void setGlass(Integer slot) {
            Inventory inventory = getInventory();

            Integer behindSlotOne = Common.getNext(slot,positions,false);
            Integer behindSlotTwo = Common.getNext(behindSlotOne,positions,false);
            Integer behindSlotThree = Common.getNext(behindSlotTwo,positions,false);

            inventory.setItem(behindSlotThree,makeGlass(CompMaterial.WHITE_STAINED_GLASS_PANE));
            inventory.setItem(behindSlotTwo,makeGlass(CompMaterial.LIGHT_BLUE_STAINED_GLASS_PANE));
            inventory.setItem(behindSlotOne,makeGlass(CompMaterial.LIGHT_BLUE_STAINED_GLASS_PANE));
            inventory.setItem(slot,makeGlass(CompMaterial.BLUE_STAINED_GLASS_PANE));
        }

        private void setAll(CompMaterial material) {
            Inventory inventory = getInventory();

            for (int i = 0; i < 8; i++)
                inventory.setItem(positions[i], makeGlass(material));
        }

        private ItemStack makeGlass(CompMaterial material) {
            return ItemCreator.of(material," ","").make();
        }

    }


}
