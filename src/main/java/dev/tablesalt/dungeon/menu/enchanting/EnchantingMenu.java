package dev.tablesalt.dungeon.menu.enchanting;

import dev.tablesalt.dungeon.database.RedisDatabase;
import dev.tablesalt.dungeon.util.ItemUtil;
import dev.tablesalt.dungeon.util.PlayerUtil;
import dev.tablesalt.gamelib.game.utils.SimpleRunnable;
import dev.tablesalt.gamelib.game.utils.TBSColor;
import dev.tablesalt.gamelib.players.PlayerCache;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.model.InventoryDrawer;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.menu.model.MenuClickLocation;
import org.mineacademy.fo.remain.CompMaterial;

public class EnchantingMenu extends Menu {

    private final Button enchantButton;

    private final BasicLoopAnimation basicLoopAnimation;

    private final EnchantingAnimation enchantingAnimation;

    public static final String ITEM_STILL_IN_ENCHANTER = "enchanted-item-left";

    private static final int ENCHANT_SLOT = 20;

    public static void openEnchantMenu(Player player) {
        new EnchantingMenu().displayTo(player);
    }

    private EnchantingMenu() {
        setTitle("&5Eternal Well");
        setSize(9 * 5);
        this.basicLoopAnimation = new BasicLoopAnimation();
        this.enchantingAnimation = new EnchantingAnimation();

        basicLoopAnimation.launch();
        enchantButton = new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType click) {
                PlayerCache cache = PlayerCache.from(player);

                if (ItemUtil.isEnchantable(getInventory().getItem(ENCHANT_SLOT)))
                    if (!cache.getTagger().getBooleanTagSafe("upgrading")) {
                        cache.getTagger().setPlayerTag("upgrading", true);
                        enchantingAnimation.launchWithColor(TBSColor.RED);
                }

            }

            @Override
            public ItemStack getItem() {
                return ItemCreator.of(CompMaterial.ENCHANTING_TABLE, "&5Eternal Well",
                        "&7Upgrade: &eTODO", "&7Cost: &6TODO", "", "&eClick to enchant!").make();
            }
        };
    }

    @Override
    protected boolean isActionAllowed(MenuClickLocation location, int slot, @Nullable ItemStack clicked, @Nullable ItemStack cursor) {


        if (MenuClickLocation.PLAYER_INVENTORY == location)
          return true;

        return MenuClickLocation.MENU == location && slot == ENCHANT_SLOT;
    }

    @Override
    protected void onMenuClick(Player player, int slot, InventoryAction action, ClickType click, ItemStack cursor, ItemStack clicked, boolean cancelled) {
        if (action == InventoryAction.PLACE_ALL && slot == ENCHANT_SLOT) {
            getInventory().setItem(ENCHANT_SLOT,cursor);


                Common.runLater(1, () -> {
                  player.setItemOnCursor(ItemCreator.of(CompMaterial.AIR).make());
                });
            }

    }




    @Override
    protected void onDisplay(InventoryDrawer drawer) {
        PlayerCache cache = PlayerCache.from(getViewer());

        ItemStack itemLeftInMenu = cache.getTagger().getPlayerTag(ITEM_STILL_IN_ENCHANTER);

        if (itemLeftInMenu != null)
            drawer.setItem(ENCHANT_SLOT,itemLeftInMenu);
    }

    @Override
    public ItemStack getItemAt(int slot) {

        if (slot == 24)
            return enchantButton.getItem();

        //makes a box 3x3 box in the menu
        if ((10 <= slot && slot <= 12) || slot == 21 || (30 >= slot && 28 <= slot) || slot == 19)
            return ItemCreator.of(CompMaterial.WHITE_STAINED_GLASS_PANE, " ").make();


        return super.getItemAt(slot);
    }

    @Override
    protected void onMenuClose(Player player, Inventory inventory) {
        PlayerCache cache = PlayerCache.from(player);

        if (basicLoopAnimation.isRunning())
            basicLoopAnimation.cancel();

        if (enchantingAnimation.isRunning()) {
            enchantingAnimation.cancel();

            if (enchantingAnimation.getEnchantedItem() != null) {
                cache.getTagger().setPlayerTag("upgrading", false);
                cache.getTagger().setPlayerTag(ITEM_STILL_IN_ENCHANTER, enchantingAnimation.getEnchantedItem());
            }

        } else if(inventory.getItem(ENCHANT_SLOT) != null)
            cache.getTagger().setPlayerTag(ITEM_STILL_IN_ENCHANTER, inventory.getItem(ENCHANT_SLOT));
        else
            cache.getTagger().setPlayerTag(ITEM_STILL_IN_ENCHANTER, NO_ITEM);
    }


    Integer[] positions = {10, 11, 12, 21, 30, 29, 28, 19};

    private class BasicLoopAnimation extends SimpleRunnable {

        Integer count = 0;

        protected BasicLoopAnimation() {
            super(-1, 3, 5);
        }

        @Override
        protected void onTick() {
            setGlass(positions[count]);

            count++;

            if (count > 7)
                count = 0;
        }


        @Override
        protected void onEnd() {

        }

        @Override
        protected void onTickError(Throwable t) {
        }
    }


    private class EnchantingAnimation extends SimpleRunnable {

        int count = 0;

        TBSColor color = TBSColor.WHITE;
        @Getter
        ItemStack enchantedItem;

        protected EnchantingAnimation() {
            super(-1, 3, 3);
        }

        public void launchWithColor(TBSColor color) {
            if (basicLoopAnimation.isRunning())
                basicLoopAnimation.cancel();

            this.color = color;

            ItemStack item = getInventory().getItem(ENCHANT_SLOT);
            enchantedItem = ItemUtil.enchantItem(item);
            setItem(ENCHANT_SLOT,NO_ITEM);


            launch();
        }

        @Override
        protected void onTick() {
            count++;

            if (count % 2 == 0)
                setAll(color);
            else
                setAll(TBSColor.WHITE);

            if (count >= 20) {
                count = 0;
                end();
            }


        }

        @Override
        protected void onEnd() {
            if (enchantedItem == null)
                Common.throwError(new NullPointerException(), "Enchanted item for " + getViewer().getName() + " is null!");


            setItem(ENCHANT_SLOT, enchantedItem);
            PlayerCache.from(getViewer()).getTagger().setPlayerTag("upgrading", false);

            setAll(TBSColor.WHITE);
            basicLoopAnimation.launch();
        }

        @Override
        protected void onTickError(Throwable t) {
            PlayerUtil.giveItem(getViewer(),enchantedItem);
        }
    }

    protected void setGlass(Integer slot) {
        Inventory inventory = getInventory();

        Integer behindSlotOne = Common.getNext(slot, positions, false);
        Integer behindSlotTwo = Common.getNext(behindSlotOne, positions, false);

        inventory.setItem(behindSlotTwo, makeGlass(TBSColor.WHITE));
        inventory.setItem(behindSlotOne, makeGlass(TBSColor.AQUA));
        inventory.setItem(slot, makeGlass(TBSColor.BLUE));
    }

    protected void setAll(TBSColor color) {
        Inventory inventory = getInventory();

        for (int i = 0; i < 8; i++)
            inventory.setItem(positions[i], makeGlass(color));
    }

    protected ItemStack makeGlass(TBSColor color) {
        return ItemCreator.of(CompMaterial.fromMaterial(color.toStainedGlassPane()), " ", "").make();
    }


}
