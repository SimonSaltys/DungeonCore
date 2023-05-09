package dev.tablesalt.dungeon.tools;

import dev.tablesalt.dungeon.game.DungeonGame;
import dev.tablesalt.dungeon.maps.DungeonMap;
import dev.tablesalt.dungeon.util.MessageUtil;
import dev.tablesalt.gameLib.lib.Common;
import dev.tablesalt.gameLib.lib.Messenger;
import dev.tablesalt.gameLib.lib.menu.model.ItemCreator;
import dev.tablesalt.gameLib.lib.remain.CompMaterial;
import dev.tablesalt.gamelib.players.PlayerCache;
import dev.tablesalt.gamelib.players.helpers.PlayerTagger;
import dev.tablesalt.gamelib.tools.GameTool;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.visual.VisualizedRegion;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExtractTool extends GameTool<DungeonGame> {
    @Getter
    private static final ExtractTool instance = new ExtractTool();

    @Override
    protected void onSuccessfulBlockClick(Player player, DungeonGame game, Block block, ClickType type) {





    }

    @Override
    protected void onSuccessfulAirClick(Player player, DungeonGame game, ClickType type) {
        DungeonMap map = game.getMapRotator().getCurrentMap();
        PlayerCache cache = PlayerCache.from(player);
        PlayerTagger tagger = cache.getTagger();

        VisualizedRegion foundRegion = map.findExtractRegion(player.getLocation());

        if (map.getExtractRegions().isEmpty() || foundRegion == null) {

            VisualizedRegion region = new VisualizedRegion();
            map.getExtractRegions().add(region);
            tagger.setPlayerTag("current-region", region);
            Common.tellNoPrefix(player,MessageUtil.makeInfo("Created new region"));
            return;
        }

        //todo delete region when in one.

    }

    @Override
    protected CompMaterial getBlockMask(Block block, Player player) {
        return CompMaterial.GOLD_BLOCK;
    }

    @Override
    public ItemStack getItem() {
        return ItemCreator.of(CompMaterial.IRON_DOOR, "&l&3EXTRACT SPAWN TOOL",
                "",
                "&b<< &fLeft click (BLock) &7– &fTo add primary point",
                "&fRight click (Block) &7– &fTo set secondary point&b>>", "",
                "&b|| &fClick air to cycle regions  &b||", "&b|| &fClick air in a region to &cdelete  &b||").makeMenuTool();
    }
}
