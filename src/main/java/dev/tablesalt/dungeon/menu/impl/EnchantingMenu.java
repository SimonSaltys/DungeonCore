package dev.tablesalt.dungeon.menu.impl;

import dev.tablesalt.dungeon.database.DungeonCache;
import dev.tablesalt.dungeon.database.EnchantableItem;
import dev.tablesalt.dungeon.database.MariaDatabase;
import dev.tablesalt.dungeon.item.ItemAttribute;
import dev.tablesalt.dungeon.item.Rarity;
import dev.tablesalt.dungeon.item.Tier;
import dev.tablesalt.dungeon.menu.TBSButton;
import dev.tablesalt.dungeon.menu.TBSMenu;
import dev.tablesalt.dungeon.util.PlayerUtil;
import dev.tablesalt.dungeon.util.TBSItemUtil;
import dev.tablesalt.dungeon.util.sound.TBSSound;
import dev.tablesalt.gamelib.game.utils.MessageUtil;
import dev.tablesalt.gamelib.game.utils.SimpleRunnable;
import dev.tablesalt.gamelib.game.utils.TBSColor;
import dev.tablesalt.gamelib.players.PlayerCache;
import dev.tablesalt.gamelib.players.helpers.PlayerTagger;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.DragType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.ItemUtil;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.model.InventoryDrawer;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.menu.model.MenuClickLocation;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.remain.CompSound;

import java.util.List;

public class EnchantingMenu extends TBSMenu {

    private TBSButton enchantButton;

    private final BasicLoopAnimation basicLoopAnimation;

    private final EnchantingAnimation enchantingAnimation;

    private static final int ENCHANT_SLOT = 20;

    public static void openEnchantMenu(Player player) {
        new EnchantingMenu().displayTo(player);
    }

    private EnchantingMenu() {
        setTitle("&5Eternal Well");
        setSize(9 * 5);
        this.basicLoopAnimation = new BasicLoopAnimation();
        this.enchantingAnimation = new EnchantingAnimation();

        slotsToPersist.add(ENCHANT_SLOT);

        basicLoopAnimation.launch();

        makeEnchantingButton();

    }


    /*----------------------------------------------------------------*/
    /* MENU LOGIC */
    /*----------------------------------------------------------------*/


    @Override
    protected boolean isActionAllowed(MenuClickLocation location, int slot, @Nullable ItemStack clicked, @Nullable ItemStack cursor) {
        if (enchantingAnimation.isRunning())
            return false;

        if (MenuClickLocation.PLAYER_INVENTORY == location)
            return true;

        return MenuClickLocation.MENU == location && slot == ENCHANT_SLOT;
    }

    @Override
    protected void onMenuClick(Player player, int slot, InventoryAction action, ClickType click, ItemStack cursor, ItemStack clicked, boolean cancelled) {
        if (action == InventoryAction.PLACE_ALL && slot == ENCHANT_SLOT) {
            playerSetItem(cursor, ENCHANT_SLOT);
            Common.runLater(0, this::restart);
        }

        if (action == InventoryAction.PICKUP_ALL && slot == ENCHANT_SLOT) {
            Common.runLater(0, this::restart);
            TBSSound.MenuPickUp.getInstance().playTo(getViewer());

        }
    }

    @Override
    protected void onMenuDrag(Player player, int slot, DragType type, ItemStack cursor) {
        if (slot == ENCHANT_SLOT) {
            playerSetItem(cursor, ENCHANT_SLOT);
            Common.runLater(0, this::restart);
        }
    }

    @Override
    protected void onRestart() {
        updateEnchantingButton();
    }

    @Override
    protected void onDisplay(InventoryDrawer drawer) {
        DungeonCache cache = DungeonCache.from(getViewer());

        EnchantableItem itemLeftInMenu = cache.getItemInEnchanter();

        if (itemLeftInMenu != null) {
            drawer.setItem(ENCHANT_SLOT, itemLeftInMenu.compileToItemStack());
        }

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
        DungeonCache dungeonCache = DungeonCache.from(player);

        if (basicLoopAnimation.isRunning())
            basicLoopAnimation.cancel();

        if (enchantingAnimation.isRunning()) {
            enchantingAnimation.cancel();

            if (enchantingAnimation.getEnchantedItem() != null) {
                cache.getTagger().setPlayerTag("upgrading", false);
                saveEnchantedItem();
            }

        } else if (inventory.getItem(ENCHANT_SLOT) != null) {
            dungeonCache.setItemInEnchanter(EnchantableItem.fromItemStack(inventory.getItem(ENCHANT_SLOT)));
        } else
            dungeonCache.setItemInEnchanter(MariaDatabase.NO_ITEM);


    }


    /*----------------------------------------------------------------*/
    /* ANIMATIONS */
    /*----------------------------------------------------------------*/


    /**
     * The idle animation where we
     * wait for a player to put in an item
     */
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
            cancel();
        }
    }


    /**
     * The animation that starts when
     * the player successfully starts to enchant an item
     */
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


            if (!TBSItemUtil.isEnchantable(item))
                return;

            EnchantableItem enchantableItem = EnchantableItem.fromItemStack(item);
            if (enchantableItem == null)
                return;


            enchantedItem = TBSItemUtil.enchantItem(getViewer(), enchantableItem);

            EnchantingSound.getInstance().playToWithItem(getViewer(), enchantableItem);

            setItem(ENCHANT_SLOT, NO_ITEM);

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
            restart();
            basicLoopAnimation.launch();
        }

        @Override
        protected void onTickError(Throwable t) {

            PlayerUtil.giveItem(getViewer(), enchantedItem);
            cancel();
        }
    }


    /*----------------------------------------------------------------*/
    /* HELPER METHODS */
    /*----------------------------------------------------------------*/

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

    private void saveEnchantedItem() {
        DungeonCache cache = DungeonCache.from(getViewer());

        if (cache == null)
            return;

        cache.setItemInEnchanter(EnchantableItem.fromItemStack(enchantingAnimation.getEnchantedItem()));

    }

    private void updateEnchantingButton() {
        ItemStack itemInMenu = getInventory().getItem(ENCHANT_SLOT);


        enchantButton.getCreator().clearLore();

        if (itemInMenu == NO_ITEM)
            enchantButton.getCreator().lore("&7Place a " + Rarity.MYTHIC + " item here", "&7To upgrade it!");

        if (TBSItemUtil.isEnchantable(itemInMenu)) {
            EnchantableItem enchantableItem = EnchantableItem.fromItemStack(itemInMenu);

            if (enchantableItem.getCurrentTier().getAsInteger() == 3)
                enchantButton.getCreator().lore("&eMax Level!");
            else {
                Tier nextTier = Tier.getNext(enchantableItem.getCurrentTier());

                enchantButton.getCreator().lore("&7Upgrade: " + nextTier.getColor().getChatColor() + nextTier.getAsRomanNumeral(),
                        "&7Cost: &6" + nextTier.getCostToUpgrade() + "g", "", "&eClick to enchant!");

            }
        }


    }

    private void makeEnchantingButton() {
        ItemCreator enchantingCreator = ItemCreator.of(CompMaterial.ENCHANTING_TABLE, "&5Eternal Well",
                "&7Place  a " + Rarity.MYTHIC + " item here", "&7To upgrade it!");
        enchantButton = new TBSButton(enchantingCreator) {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType click) {

                ItemStack itemStack = getInventory().getItem(ENCHANT_SLOT);
                if (itemStack == null)
                    return;


                EnchantableItem enchantableItem = EnchantableItem.fromItemStack(itemStack);
                attemptUpgrade(player, enchantableItem);

            }
        };
    }

    private void attemptUpgrade(Player player, EnchantableItem item) {
        PlayerCache cache = PlayerCache.from(player);
        DungeonCache dungeonCache = DungeonCache.from(player);

        if (canUpgrade(player, item)) {
            Tier tierToUpgrade = Tier.getNext(item.getCurrentTier());

            cache.getTagger().setPlayerTag("upgrading", true);
            enchantingAnimation.launchWithColor(tierToUpgrade.getColor());
            dungeonCache.takeMoney(tierToUpgrade.getCostToUpgrade());
        } else {

            if (item != null && item.getCurrentTier().equals(Tier.THREE)) {
                animateTitle("&4&lItem is Max Level!");
                return;
            }

            animateTitle("&4&lNot Enough Money!");

        }
    }

    private boolean canUpgrade(Player player, EnchantableItem item) {
        PlayerTagger tagger = PlayerCache.from(player).getTagger();

        DungeonCache dungeonCache = DungeonCache.from(player);


        if (item != null && !item.getCurrentTier().equals(Tier.THREE))
            if (dungeonCache.getMoney() >= Tier.getNext(item.getCurrentTier()).getCostToUpgrade())
                return !tagger.getBooleanTagSafe("upgrading");

        return false;
    }

    private static final class EnchantingSound {

        @Getter
        private static final EnchantingSound instance = new EnchantingSound();

        public void playToWithItem(Player player, EnchantableItem itemEnchanted) {
            new EnchantingSoundRunnable(player, itemEnchanted).launch();
        }


        private static class EnchantingSoundRunnable extends TBSSound.SimpleSoundRunnable {

            final float[] repeats = new float[]{0.4F, 0.6F, 0.8F, 1.0F, 1.2F, 1.4F};
            float pitchShift = repeats[0];

            int count = 0;

            float repeat = 0.0F;

            private final Player player;

            private final EnchantableItem item;


            public EnchantingSoundRunnable(Player player, EnchantableItem item) {
                super(30, 2, 0);

                this.player = player;
                this.item = item;
            }


            @Override
            protected void onTick() {

                if (Menu.getMenu(player) == null)
                    end();

                if (count >= repeats.length) {
                    count = 0;
                    repeat += 0.2F;
                }

                pitchShift = repeats[Math.max(count, 0)];
                pitchShift += repeat;

                CompSound.NOTE_STICKS.play(player, 1, pitchShift);

                if (getCounter() % 2 == 0)
                    CompSound.ITEM_PICKUP.play(player, 1, pitchShift);


                count++;
                super.onTick();
            }

            @Override
            protected void onEnd() {
                ItemAttribute mostRecentAdded = item.getLastAdded();

                if (mostRecentAdded == null) return;

                if (mostRecentAdded.getRarity() == Rarity.EPIC || mostRecentAdded.getRarity() == Rarity.MYTHIC) {
                    TextComponent component = makeTextComponent();
                    MessageUtil.forAllPlayersNotInGame(receiver -> receiver.sendMessage(component));

                    TBSSound.GoodItemEnchanted.getInstance().playTo(player);
                }

            }

            private TextComponent makeTextComponent() {
                TextColor mainColor = item.getCurrentTier().getColor().getTextColor();

                return Component.text(Common.colorize(MessageUtil.makeInfo(" &7" + player.getName() + " just rolled a cool ")))

                        .append(Component.text()
                                .content(Rarity.MYTHIC + " " + ItemUtil.bountifyCapitalized(item.getName()) + " ").color(mainColor)
                                .hoverEvent(HoverEvent.showText(Component.text().content(Common.colorize(loreToString()))))
                                .append(Component.text(item.getCurrentTier().getAsRomanNumeral(), Style.style(mainColor, TextDecoration.BOLD))).build());

            }

            private String loreToString() {
                StringBuilder builder = new StringBuilder();

                builder.append("&7" + player.getName() + "'s " +
                        item.getCurrentTier().getColor().getChatColor()
                        + Rarity.MYTHIC + " " + item.getName() + "&l " + item.getCurrentTier().getAsRomanNumeral()
                        + "\n");

                List<String> lores = item.getLores();

                for (int i = 0; i < lores.size(); i++) {
                    builder.append(lores.get(i));

                    if (i != lores.size() - 1)
                        builder.append("\n");

                }

                return builder.toString();
            }

        }
    }
}
