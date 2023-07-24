package dev.tablesalt.dungeon.tools;

import dev.tablesalt.dungeon.DungeonSettings;
import dev.tablesalt.dungeon.util.EntityUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.ChatUtil;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompChatColor;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.visual.VisualTool;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)

public final class EnchantingWellTool extends VisualTool {

    @Getter
    private static final EnchantingWellTool instance = new EnchantingWellTool();


    public static final String DISPLAY_NAME = "Enchanted Well";


    @Override
    protected CompMaterial getBlockMask(Block block, Player player) {
        return CompMaterial.ENCHANTING_TABLE;
    }

    @Override
    protected void handleBlockClick(Player player, ClickType click, Block block) {
        DungeonSettings dungeonSettings = DungeonSettings.getInstance();
        Location nextLocation = block.getLocation().add(0.5,1.2,0.5);

        updateHologram(dungeonSettings.getEnchantingLocation(), nextLocation);

        dungeonSettings.setEnchantingLocation(block.getLocation());
    }

    @Override
    protected List<Location> getVisualizedPoints(Player player) {
        return Arrays.asList(DungeonSettings.getInstance().getEnchantingLocation());
    }

    @Override
    public ItemStack getItem() {
        return ItemCreator.of(CompMaterial.CHEST, "&l&3ENCHANTING SPAWN TOOL",
                "",
                "&b<< &fLeft click &7– &fTo remove or add a point",
                "&fRight click a point &7– &fTo open configuration menu &b>>").makeMenuTool();
    }

    private void updateHologram(Location previous, Location next) {
        TextDisplay display = EntityUtil.getClosestTextDisplay(previous, 2.0);

        if (display == null) {
            display = EntityUtil.createTextDisplay(next, ChatUtil.generateGradient(DISPLAY_NAME, CompChatColor.LIGHT_PURPLE, CompChatColor.GOLD));
            display.setCustomName(DISPLAY_NAME);
        }

        display.teleport(next);
    }
}
