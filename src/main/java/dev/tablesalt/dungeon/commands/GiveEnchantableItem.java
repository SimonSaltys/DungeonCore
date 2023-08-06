package dev.tablesalt.dungeon.commands;

import dev.tablesalt.dungeon.nms.PlayerCorpse;
import dev.tablesalt.gamelib.commands.GameSubCommand;

public final class GiveEnchantableItem extends GameSubCommand {
    private GiveEnchantableItem() {
        super("give", 0, "", "adds attribute to held item");
    }

    @Override
    protected void onCommand() {
        new PlayerCorpse(getPlayer()).makeCorpse();
//
//        DungeonCache cache = DungeonCache.from(getPlayer());
//        cache.giveMoney(10000);
//        Common.broadcast("Money " + cache.getMoney());
//
//
//        EnchantableItem enchantableItem = new EnchantableItem(
//                CompMaterial.LEATHER_CHESTPLATE.name(),
//                Material.LEATHER_CHESTPLATE,
//                new HashMap<>(),
//                Tier.NONE,
//                UUID.randomUUID());
//
//        PlayerUtil.giveItem(getPlayer(), enchantableItem.compileToItemStack());
    }
}
