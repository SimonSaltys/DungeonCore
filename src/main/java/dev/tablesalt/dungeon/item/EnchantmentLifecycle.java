package dev.tablesalt.dungeon.item;

import dev.tablesalt.dungeon.database.EnchantableItem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * This interface streamlines the management of enchantment-related
 * processes within the ItemAttribute framework. Instead of scattering
 * overrides across multiple methods in ItemAttribute, the start and
 * stop actions are consolidated here. By doing so, these actions are
 * triggered uniformly through corresponding events. Consequently, you can
 * maintain a single method that efficiently handles both the initiation
 * and cessation of enchantment effects.
 */
public interface EnchantmentLifecycle {

    void start(Player player, EnchantableItem item, Tier tier);

    void stop(Player player, @Nullable EnchantableItem item, @Nullable Tier tier);


}
