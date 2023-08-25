package dev.tablesalt.dungeon.commands;

import dev.tablesalt.dungeon.database.DungeonCache;
import dev.tablesalt.dungeon.database.EnchantableItem;
import dev.tablesalt.gamelib.commands.GameSubCommand;
import org.mineacademy.fo.PlayerUtil;

public final class GiveEnchantableItem extends GameSubCommand {
    private GiveEnchantableItem() {
        super("give", 0, "", "adds attribute to held item");
    }

    @Override
    protected void onCommand() {

        DungeonCache cache = DungeonCache.from(getPlayer());
        cache.giveMoney(100000);

        for (int i = 0; i < 10; i++)
            PlayerUtil.addItems(getPlayer().getInventory(), EnchantableItem.makeWeapon().compileToItemStack());

        for (int i = 0; i < 10; i++)
            PlayerUtil.addItems(getPlayer().getInventory(), EnchantableItem.makeArmor().compileToItemStack());

    }
}
