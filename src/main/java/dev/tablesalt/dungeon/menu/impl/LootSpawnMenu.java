package dev.tablesalt.dungeon.menu.impl;

import dev.tablesalt.dungeon.maps.spawnpoints.LootPoint;
import dev.tablesalt.dungeon.util.PlayerUtil;

import org.bukkit.command.Command;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.conversation.SimpleConversation;
import org.mineacademy.fo.conversation.SimpleDecimalPrompt;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.MenuQuantitable;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.button.ButtonConversation;
import org.mineacademy.fo.menu.button.ButtonMenu;
import org.mineacademy.fo.menu.model.MenuClickLocation;
import org.mineacademy.fo.remain.CompMaterial;

import javax.swing.plaf.ButtonUI;
import java.util.Arrays;
import java.util.Stack;

public class LootSpawnMenu extends Menu {

    private final LootPoint point;


    private final ButtonMenu dropsMenu;



    private LootSpawnMenu(LootPoint point) {
        this.point = point;
        setTitle("&0&lMonster Point Menu");
        setSize(9);

        dropsMenu = new ButtonMenu(new PercentageChanceMenu(point),CompMaterial.DIAMOND,"Drop Configuration","");

    }

    public static void openConfigMenu(Player player, LootPoint point) {
        new LootSpawnMenu(point).displayTo(player);
    }

    @Override
    public ItemStack getItemAt(int slot) {

        if (getCenterSlot() == slot)
            return dropsMenu.getItem();

        return NO_ITEM;
    }

    @Override
    public Menu newInstance() {
        return new LootSpawnMenu(point);
    }


    private class PercentageChanceMenu extends Menu {

        private final LootPoint lootPoint;


        protected final ButtonConversation mysticDropsButton;

        public PercentageChanceMenu(LootPoint lootPoint) {
            super(LootSpawnMenu.this);

            this.lootPoint = lootPoint;

            mysticDropsButton = makeMysticConversation();

        }


        @Override
        public ItemStack getItemAt(int slot) {

            if (slot == 9)
                return mysticDropsButton.getItem();


            return NO_ITEM;
        }

        private ButtonConversation makeMysticConversation() {

            SimpleConversation conversation = new SimpleConversation() {
                @Override
                protected Prompt getFirstPrompt() {
                    return new ConfigurationPrompt("&emystic drop chance %") {
                        @Override
                        public void setConfiguring(double input) {
                            lootPoint.setMysticDropChance(input);
                        }

                        @Override
                        public Prompt getNextPrompt() {
                            return new ConfigurationPrompt("&emax mystic drops") {
                                @Override
                                public void setConfiguring(double input) {
                                    lootPoint.setMaxMysticDrops((int) Math.round(input));
                                }

                                @Override
                                public Prompt getNextPrompt() {
                                    return END_OF_CONVERSATION;
                                }
                            };
                        }
                    };
                }
            };

            return new ButtonConversation(conversation,CompMaterial.LEATHER_CHESTPLATE,"Mystic Configuration",
                    "Drop Chance: &e" + lootPoint.getMysticDropChance() + "%",
                    "Max Drops: &e" + lootPoint.getMaxMysticDrops());
       }

        @Override
        public Menu newInstance() {
            return new PercentageChanceMenu(point);
        }
    }



    private abstract class ConfigurationPrompt extends SimpleDecimalPrompt {

        private final String configuring;

        public ConfigurationPrompt(String configuring) {
            this.configuring = configuring;
        }

        public abstract void setConfiguring(double input);

        public abstract Prompt getNextPrompt();


        @Override
        protected String getPrompt(ConversationContext ctx) {
            return "Please enter the " + configuring;
        }


        @Override
        protected boolean isInputValid(ConversationContext context, String input) {
           if (Valid.isDecimal(input)) {
               double inputDouble = Double.parseDouble(input);

               return inputDouble >= 0 && inputDouble <= 100;
           }
           return false;
        }

        @Override
        protected Prompt acceptValidatedInput(ConversationContext context, double input) {
            setConfiguring(input);

            return getNextPrompt();
        }
    }
}
