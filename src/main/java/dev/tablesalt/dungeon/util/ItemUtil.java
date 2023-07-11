package dev.tablesalt.dungeon.util;

import lombok.experimental.UtilityClass;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.remain.CompMaterial;

@UtilityClass
public class ItemUtil {



    public ItemStack makeBaseItem(CompMaterial material, String name) {
        return ItemCreator.of(material,"&9" + name,"").make();
    }



}
