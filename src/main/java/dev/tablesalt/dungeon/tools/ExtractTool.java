package dev.tablesalt.dungeon.tools;

import dev.tablesalt.dungeon.game.DungeonGame;
import dev.tablesalt.dungeon.maps.DungeonMap;
import dev.tablesalt.gamelib.game.utils.MessageUtil;
import dev.tablesalt.gamelib.players.PlayerCache;
import dev.tablesalt.gamelib.tools.GameTool;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.visual.VisualizedRegion;

import java.text.Format;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExtractTool extends GameTool<DungeonGame> {
    @Getter
    private static final ExtractTool instance = new ExtractTool();

    @Override
    protected void onSuccessfulBlockClick(Player player, DungeonGame game, Block block, ClickType click) {
        boolean isPrimaryClick = (click == ClickType.LEFT);

        DungeonMap map = game.getMapRotator().getCurrentMap();
        PlayerCache cache = PlayerCache.from(player);
        VisualizedRegion currentRegion = cache.getTagger().getPlayerTag("current-region");

        if (currentRegion == null)
            currentRegion = switchExtractRegion(player,game);


        if (isPrimaryClick)
            currentRegion.setPrimary(block.getLocation());
        else
            currentRegion.setSecondary(block.getLocation());

       player.sendMessage(MessageUtil.makeInfo("Set " + (isPrimaryClick ? "&6primary" : "&6secondary") + " &rextract point"));

        map.save();
    }

    @Override
    protected void onSuccessfulAirClick(Player player, DungeonGame game, ClickType click) {
        switchExtractRegion(player,game);
    }

    private VisualizedRegion switchExtractRegion(Player player, DungeonGame game) {
        DungeonMap map = game.getMapRotator().getCurrentMap();
        PlayerCache cache = PlayerCache.from(player);
        VisualizedRegion currentRegion = cache.getTagger().getPlayerTag("current-region");

        if (!validateRegion(currentRegion,player)) return null;

        if (map.getExtractRegions().size() < map.getExtractRegionAmount())
            map.addExtractRegion(new VisualizedRegion());

        List<VisualizedRegion> extractRegions = map.getVisualizedExtractRegions();

       VisualizedRegion nextRegion = Common.getNext(currentRegion == null ? extractRegions.get(0) : currentRegion,extractRegions,true);

       if (nextRegion == null)
           nextRegion = extractRegions.get(0);

        cache.getTagger().setPlayerTag("current-region",nextRegion);

        player.sendMessage(MessageUtil.makeInfo("Now Editing extract region <region> !",
                Formatter.number("region", (extractRegions.indexOf(nextRegion) + 1))));

        return nextRegion;
    }

    private boolean validateRegion(VisualizedRegion region, Player player) {
        if (region != null && !region.isWhole()) {
            player.sendMessage(MessageUtil.makeError("Make sure this region is whole before proceeding"));
            return false;
        }

        return true;
    }


    @Override
    protected VisualizedRegion getVisualizedRegion(Player player) {
        VisualizedRegion currentRegion = PlayerCache.from(player).getTagger().getPlayerTag("current-region");

        if (currentRegion == null || !currentRegion.isWhole())
            return null;

        return currentRegion;
    }

    protected String getBlockName(Block block, Player player) {
        return "&l[&fExtract Region&l]";
    }


    @Override
    protected CompMaterial getBlockMask(Block block, Player player) {
        return CompMaterial.IRON_BLOCK;
    }

    @Override
    public ItemStack getItem() {
        return ItemCreator.of(CompMaterial.IRON_DOOR, "&l&3EXTRACT SPAWN TOOL",
                "",
                "&b<< &fLeft click (BLock) &7– &fTo add primary point",
                "&fRight click (Block) &7– &fTo set secondary point&b>>", "",
                "&b|| &fClick air to cycle regions  &b||").makeMenuTool();
    }
}
