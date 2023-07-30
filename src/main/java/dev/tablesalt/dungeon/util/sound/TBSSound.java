package dev.tablesalt.dungeon.util.sound;


import dev.tablesalt.dungeon.database.EnchantableItem;
import dev.tablesalt.dungeon.item.ItemAttribute;
import dev.tablesalt.dungeon.item.Rarity;
import dev.tablesalt.dungeon.util.PlayerUtil;
import dev.tablesalt.gamelib.game.utils.SimpleRunnable;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.commons.lang3.RandomUtils;
import org.bukkit.entity.Player;
import org.mineacademy.fo.RandomUtil;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.remain.CompSound;

import java.util.Set;

@UtilityClass
public class TBSSound {

    public interface GameSound {
        void playTo(Player player);
    }


    /*----------------------------------------------------------------*/
    /* Menu related sounds */
    /*----------------------------------------------------------------*/

    public final class EnchantingSound {

        @Getter
        private static final EnchantingSound instance = new EnchantingSound();

        public void playToWithItem(Player player, EnchantableItem itemEnchanted) {
            new EnchantingSoundRunnable(player, itemEnchanted).launch();
        }



        private static class EnchantingSoundRunnable extends SimpleSoundRunnable {

            final float[] repeats = new float[]{0.4F, 0.6F, 0.8F, 1.0F, 1.2F, 1.4F};
            float pitchShift = repeats[0];

            int count = 0;

            float repeat = 0.0F;

            private final Player player;

            private final EnchantableItem item;

            private final Set<ItemAttribute> attributesBeforeEnchant;

            public EnchantingSoundRunnable(Player player, EnchantableItem item) {
                super(30, 2, 0);

                this.player = player;
                this.item = item;
                attributesBeforeEnchant = item.getAttributeTierMap().keySet();
            }



            @Override
            protected void onTick() {

                if (Menu.getMenu(player) == null)
                    cancel();

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
                Set<ItemAttribute> attributesAfterEnchant = item.getAttributeTierMap().keySet();

                for (ItemAttribute attribute : attributesAfterEnchant) {
                    if (attribute.getRarity().equals(Rarity.EPIC) || attribute.getRarity().equals(Rarity.MYTHIC)) {
                        TextComponent content = makeTextComponent(attribute.getRarity());

                        for (Player receiver : PlayerUtil.getPlayersNotInGame())
                            receiver.sendMessage(content);
                    }
                }
            }

            private TextComponent makeTextComponent(Rarity rarity) {
                TextColor mainColor = item.getCurrentTier().getColor().getTextColor();

                TextComponent title = Component.text()
                        .content(Rarity.MYTHIC + " " + item.getName() + " ").color(mainColor)
                        .append(Component.text(item.getCurrentTier().getAsRomanNumeral(),Style.style(mainColor,TextDecoration.BOLD))).build();

                return title;

            }
        }
    }


    public final class MenuPlace implements GameSound {

        @Getter
        private static final MenuPlace instance = new MenuPlace();

        @Override
        public void playTo(Player player) {
            CompSound.ITEM_PICKUP.play(player, 1, RandomUtils.nextFloat(1.5F, 2F));


        }
    }

    public final class MenuPickUp implements GameSound {
        @Getter
        private static final MenuPickUp instance = new MenuPickUp();

        @Override
        public void playTo(Player player) {
            CompSound.ITEM_PICKUP.play(player, 1, RandomUtils.nextFloat(0.8F, 1.2F));


        }
    }

    // ------–------–------–------–------–------–------–------–------–------–------–------–----
    // Game related sounds
    // ------–------–------–------–------–------–------–------–------–------–------–------–----
    public final class MoneyPickup implements GameSound {
        @Getter
        private static final MoneyPickup instance = new MoneyPickup();

        @Override
        public void playTo(Player player) {
            float pitchShift = RandomUtils.nextFloat(1.2F, 1.4F);

            CompSound.ENTITY_EXPERIENCE_ORB_PICKUP.play(player, 1, pitchShift);
        }
    }

    public final class Rewarded implements GameSound {
        @Getter
        private static final Rewarded instance = new Rewarded();

        @Override
        public void playTo(Player player) {

            new SimpleSoundRunnable(4, 3) {
                float pitchShift = 1.2F;

                @Override
                protected void onTick() {
                    super.onTick();
                    CompSound.ENTITY_EXPERIENCE_ORB_PICKUP.play(player, 1, pitchShift);
                    pitchShift += 0.3F;
                }
            }.launch();

            CompSound.ENTITY_PLAYER_LEVELUP.play(player, 1, 1.2F);
        }
    }

    public final class Kill implements GameSound {

        @Getter
        private static final Kill instance = new Kill();

        @Override
        public void playTo(Player player) {
            CompSound.NOTE_PLING.play(player, 1, 10);
        }
    }

    /**
     * The sound to be played when a player breaks something
     */
    public final class Broken implements GameSound {

        @Getter
        private static final Broken instance = new Broken();

        @Override
        public void playTo(Player player) {
            CompSound.ITEM_SHIELD_BREAK.play(player, 1, 1);
            CompSound.ITEM_BREAK.play(player, 1, 1);

        }
    }


    // ------–------–------–------–------–------–------–------–------–------–------–------–----
    // Kit Related sounds
    // ------–------–------–------–------–------–------–------–------–------–------–------–----

    /**
     * The sound to be played when a player gets buffed by another player
     */
    public final class Buffed implements GameSound {

        @Getter
        private static final Buffed instance = new Buffed();

        @Override
        public void playTo(Player player) {
            CompSound.BLOCK_RESPAWN_ANCHOR_CHARGE.play(player, 1, 1.5F);
            CompSound.SUCCESSFUL_HIT.play(player, 1, 2F);
            CompSound.LEVEL_UP.play(player, 1, 1.5F);
        }
    }

    /**
     * The sound to be played when a player buffs another players
     */
    public final class Buffer implements GameSound {

        @Getter
        private static final Buffer instance = new Buffer();

        @Override
        public void playTo(Player player) {
            new SimpleSoundRunnable(3, 5) {
                @Override
                protected void onTick() {
                    super.onTick();
                    CompSound.ENTITY_LLAMA_SPIT.play(player, 1, RandomUtil.getRandom().nextFloat(0, 0.5F) * -1);
                }
            }.launch();
            CompSound.ENTITY_IRON_GOLEM_REPAIR.play(player, 1, -0.5F);
        }
    }

    public final class NegatedDamage implements GameSound {

        @Getter
        private static final NegatedDamage instance = new NegatedDamage();

        @Override
        public void playTo(Player player) {
            CompSound.ITEM_SHIELD_BLOCK.play(player, 1, RandomUtil.getRandom().nextFloat(1, 1.3F));
            CompSound.ARROW_HIT.play(player, 1, RandomUtil.getRandom().nextFloat(1, 2F));
        }
    }

    public final class Scanned implements GameSound {

        @Getter
        private static final Scanned instance = new Scanned();

        @Override
        public void playTo(Player player) {
            new SimpleSoundRunnable(3, 5) {
                @Override
                protected void onTick() {
                    super.onTick();
                    CompSound.BLOCK_AZALEA_LEAVES_HIT.play(player, 1, 0.7F);
                }
            }.launch();
            CompSound.BLAZE_HIT.play(player, 1, 0);

        }
    }


    // ------–------–------–------–------–------–------–------–------–------–------–------–----
    // Private Access and Utility methods
    // ------–------–------–------–------–------–------–------–------–------–------–------–----

    public void playToPlayers(GameSound sound, Player... players) {
        for (Player player : players)
            sound.playTo(player);
    }


    private class SimpleSoundRunnable extends SimpleRunnable {
        private final int iterations;

        @Getter
        private int counter = 0;

        public SimpleSoundRunnable(int iterations) {
            this(iterations, 0, 0);
        }

        public SimpleSoundRunnable(int iterations, int tickRate) {
            this(iterations, tickRate, 0);
        }

        public SimpleSoundRunnable(int iterations, int tickRate, int startDelay) {
            super(-1, startDelay, tickRate);
            this.iterations = iterations;
        }

        @Override
        protected void onTick() {

            if (counter >= iterations - 1)
               end();

            counter++;
        }

        @Override
        protected void onEnd() {

        }
    }
}
