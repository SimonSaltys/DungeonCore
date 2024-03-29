package dev.tablesalt.dungeon.menu.impl;

import dev.tablesalt.dungeon.maps.spawnpoints.MonsterPoint;
import dev.tablesalt.dungeon.util.TBSPlayerUtil;
import dev.tablesalt.gamelib.game.utils.MessageUtil;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mineacademy.fo.ItemUtil;
import org.mineacademy.fo.conversation.SimplePrompt;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.MenuPagged;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.button.ButtonConversation;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

import java.util.Arrays;
import java.util.stream.Collectors;

public class MonsterSpawnMenu extends Menu {

    private final Button monsterSelectButton;

    private final ButtonConversation radiusButton;

    private final ButtonConversation amountButton;

    private final MonsterPoint point;

    private MonsterSpawnMenu(MonsterPoint point) {
        this.point = point;
        setTitle("&0&lMonster Point Menu");
        setSize(9);


        monsterSelectButton = makeMonsterSelectButton(point);
        radiusButton = makeSpawnRadiusButton(point);
        amountButton = makeAmountButton(point);
    }


    public static void openConfigMenu(Player player, MonsterPoint point) {
        new MonsterSpawnMenu(point).displayTo(player);
    }

    @Override
    public Menu newInstance() {
        return new MonsterSpawnMenu(point);
    }

    @Override
    public ItemStack getItemAt(int slot) {

        if (slot == getCenterSlot())
            return monsterSelectButton.getItem();

        if (radiusButton != null && slot == 0)
            return radiusButton.getItem();

        if (amountButton != null && slot == 8)
            return amountButton.getItem();

        return NO_ITEM;
    }

    private final class MonsterSelectMenu extends MenuPagged<EntityType> {

        private final MonsterPoint point;

        private MonsterSelectMenu(MonsterPoint point) {
            super(MonsterSpawnMenu.this, Arrays.stream(EntityType.values()).filter(entityType -> entityType.isSpawnable() && entityType.isAlive())
                    .collect(Collectors.toList()));

            this.point = point;
        }

        @Override
        protected ItemStack convertToItemStack(EntityType entityType) {
            return ItemCreator.ofEgg(entityType).name(ItemUtil.bountifyCapitalized(entityType.name()))
                    .lore("",
                            "&b-- &fClick to select",
                            "&fthis mob to spawn &b--")
                    .glow(point.getEntity() == entityType).make();
        }

        @Override
        protected void onPageClick(Player player, EntityType entityType, ClickType clickType) {
            point.setEntity(entityType);
            restartMenu();
            TBSPlayerUtil.getMapSafe(player).save();
        }
    }

    private static class RadiusPrompt extends SimplePrompt {

        private static final String NO_RADIUS = "No Radius";

        private final MonsterPoint point;

        public RadiusPrompt(MonsterPoint point) {
            this.point = point;
        }

        @Override
        protected String getCustomPrefix() {
            return MessageUtil.getPromptPrefix();
        }

        @Override
        protected String getPrompt(ConversationContext conversationContext) {
            return "Enter the radius for the monster to spawn in. (0-40) or &7\"No Radius\" &fto disable.";
        }

        @Override
        protected boolean isInputValid(ConversationContext context, String input) {
            if (input.equalsIgnoreCase(NO_RADIUS))
                return true;

            try {
                double radius = Double.parseDouble(input);

                if (radius > 0 && radius < 40)
                    return true;
            } catch (NumberFormatException e) {
                return false;
            }

            return false;
        }

        @Nullable
        @Override
        protected Prompt acceptValidatedInput(@NotNull ConversationContext conversationContext, @NotNull String input) {

            if (input.equalsIgnoreCase(NO_RADIUS))
                point.setTriggerRadius(-1);
            else {
                double radius = Double.parseDouble(input);
                point.setTriggerRadius(radius);
            }

            Player player = (Player) conversationContext.getForWhom();
            TBSPlayerUtil.getMapSafe(player).save();

            return END_OF_CONVERSATION;
        }
    }


    private static class AmountConversation extends SimplePrompt {
        private final MonsterPoint point;

        public AmountConversation(MonsterPoint point) {
            this.point = point;
        }

        @Override
        protected boolean isInputValid(ConversationContext context, String input) {

            try {
                int radius = Integer.parseInt(input);

                if (radius > 0 && radius < 10)
                    return true;
            } catch (NumberFormatException e) {
                return false;
            }

            return false;
        }

        @Override
        protected String getCustomPrefix() {
            return MessageUtil.getPromptPrefix();
        }

        @Override
        protected String getPrompt(ConversationContext conversationContext) {
            return "Enter the amount of monsters to spawn. (1-9) default is 1.";
        }

        @Nullable
        @Override
        protected Prompt acceptValidatedInput(@NotNull ConversationContext conversationContext, @NotNull String input) {

            int amount = Integer.parseInt(input);
            point.setAmountToSpawn(amount);

            Player player = (Player) conversationContext.getForWhom();
            TBSPlayerUtil.getMapSafe(player).save();

            return END_OF_CONVERSATION;
        }
    }

    private Button makeMonsterSelectButton(MonsterPoint spawnPoint) {
        return new Button() {
            @Override
            public void onClickedInMenu(Player player, Menu menu, ClickType clickType) {
                new MonsterSelectMenu(spawnPoint).displayTo(player);
            }

            @Override
            public ItemStack getItem() {
                return ItemCreator.ofEgg(spawnPoint.getEntity()).name("&l&3Current Entity: " +
                        "&7" + ItemUtil.bountifyCapitalized(spawnPoint.getEntity().name())).make();
            }
        };
    }

    private ButtonConversation makeSpawnRadiusButton(MonsterPoint spawnPoint) {
        return new ButtonConversation(new RadiusPrompt(spawnPoint), ItemCreator.of(CompMaterial.COMPASS,
                "&l&3Spawn Radius: &7" + spawnPoint.getTriggerRadius()));
    }

    private ButtonConversation makeAmountButton(MonsterPoint spawnPoint) {
        return new ButtonConversation(new AmountConversation(spawnPoint), ItemCreator.of(CompMaterial.CHEST,
                "&l&3Amount to Spawn: &7" + spawnPoint.getAmountToSpawn()));
    }
}


