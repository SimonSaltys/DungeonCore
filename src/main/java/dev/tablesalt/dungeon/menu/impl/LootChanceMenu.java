package dev.tablesalt.dungeon.menu.impl;

import dev.tablesalt.dungeon.configitems.LootChance;
import dev.tablesalt.dungeon.item.Rarity;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.conversation.SimpleConversation;
import org.mineacademy.fo.conversation.SimpleDecimalPrompt;
import org.mineacademy.fo.conversation.SimplePrompt;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.MenuPagged;
import org.mineacademy.fo.menu.button.ButtonConversation;
import org.mineacademy.fo.menu.button.ButtonRemove;
import org.mineacademy.fo.remain.CompMaterial;

public class LootChanceMenu extends MenuPagged<LootChance> {

    final ButtonConversation createButton;



    private LootChanceMenu() {
        super(LootChance.getChances());

        createButton = makeCreateButton();

    }


    private ButtonConversation makeCreateButton() {
        SimpleConversation conversation = new SimpleConversation() {

            @Override
            protected Prompt getFirstPrompt() {
                return new SimplePrompt() {
                    @Override
                    protected String getPrompt(ConversationContext context) {
                        return "Enter the name of this chance";
                    }

                    @Override
                    protected boolean isInputValid(ConversationContext context, String input) {
                       return LootChance.getLootChance(input) == null;
                    }

                    @Nullable
                    @Override
                    protected Prompt acceptValidatedInput(@NotNull ConversationContext conversationContext, @NotNull String s) {

                        LootChance.makeLootChance(s);

                       return END_OF_CONVERSATION;
                    }
                };
            }
        };

        return new ButtonConversation(conversation, CompMaterial.GREEN_CONCRETE, "Create Chance","");

    }

    @Override
    public ItemStack getItemAt(int slot) {
        if (slot == 9)
            return createButton.getItem();

        return super.getItemAt(slot);
    }

    @Override
    protected ItemStack convertToItemStack(LootChance chance) {
        return chance.convertToItem();
    }

    @Override
    protected void onPageClick(Player player, LootChance item, ClickType click) {
        new LootChanceConfigMenu(item).displayTo(player);
    }

    public static void openMenu(Player player) {
        new LootChanceMenu().displayTo(player);
    }

    @Override
    public Menu newInstance() {
        return new LootChanceMenu();
    }

    private class LootChanceConfigMenu extends Menu {

         final LootChance chance;
         final ButtonConversation mythicDropsButton;

         final ButtonConversation goldDropsButton;

         final ButtonRemove buttonRemove;

        private LootChanceConfigMenu(LootChance chance) {
            super(LootChanceMenu.this);
            this.chance = chance;
            mythicDropsButton = makeMysticConversation();
            goldDropsButton = makeGoldConversation();

            this.buttonRemove = new ButtonRemove(LootChanceMenu.this,"LootChance",chance.getName(),
                    () -> LootChance.removeChance(chance));


        }

        @Override
        public ItemStack getItemAt(int slot) {

            if (slot == 10)
                return mythicDropsButton.getItem();

            if (slot == getCenterSlot())
                return goldDropsButton.getItem();


            if (slot == 16)
                return buttonRemove.getItem();


            return NO_ITEM;
        }

        private ButtonConversation makeMysticConversation() {

            SimpleConversation conversation = new SimpleConversation() {
                @Override
                protected Prompt getFirstPrompt() {
                    return new ConfigurationPrompt("&e" + Rarity.MYTHIC + " drop chance %") {
                        @Override
                        public void setConfiguring(double input) {
                            chance.setMythicDropChance(input);
                        }

                        @Override
                        public Prompt getNextPrompt() {
                            return new ConfigurationPrompt("&emax " + Rarity.MYTHIC + " drops") {
                                @Override
                                public void setConfiguring(double input) {
                                    chance.setMaxMythicDrops((int) Math.round(input));
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

            return new ButtonConversation(conversation, CompMaterial.GOLDEN_SWORD, "Mystic Configuration",
                    "Drop Chance: &e" + chance.getMythicDropChance() + "%",
                    "Max Drops: &e" + chance.getMaxMythicDrops());
        }

        public ButtonConversation makeGoldConversation() {

            SimpleConversation conversation = new SimpleConversation() {
                @Override
                protected Prompt getFirstPrompt() {
                    return new ConfigurationPrompt("&6gold drop chance %") {
                        @Override
                        public void setConfiguring(double input) {
                            chance.setGoldDropChance(input);
                        }

                        @Override
                        public Prompt getNextPrompt() {
                            return new ConfigurationPrompt("&6max gold drops") {
                                @Override
                                public void setConfiguring(double input) {
                                    chance.setMaxGoldDrops((int) Math.round(input));
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

            return new ButtonConversation(conversation, CompMaterial.GOLD_INGOT, "Gold Configuration",
                    "Drop Chance: &e" + chance.getGoldDropChance() + "%",
                    "Max Drops: &e" + chance.getMaxGoldDrops());
        }

        @Override
        public Menu newInstance() {
            return new LootChanceConfigMenu(chance);
        }
    }


    private abstract static class ConfigurationPrompt extends SimpleDecimalPrompt {

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
