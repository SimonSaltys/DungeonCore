package dev.tablesalt.dungeon.item.impl;

import dev.tablesalt.dungeon.item.AttributeActions;
import dev.tablesalt.dungeon.item.ItemAttribute;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.Common;

public final class TestAttribute extends ItemAttribute {

    @Getter
    private static final TestAttribute instance = new TestAttribute();
    private final TestAttributeActions actions;

    public TestAttribute() {
        super("Test");

        this.actions = new TestAttributeActions();
    }

    @Override
    protected AttributeActions getActions() {
        return actions;
    }


    public class TestAttributeActions extends AttributeActions {

        protected TestAttributeActions() {

        }

        @Override
        protected void onClick(Player player, ItemStack itemClicked, PlayerInteractEvent event) {
            Common.broadcast("Clicked with attribute " + getTag());
        }
    }
}
