package dev.tablesalt.dungeon.model.effects;

import de.slikey.effectlib.EffectManager;
import dev.tablesalt.dungeon.DungeonPlugin;
import dev.tablesalt.gamelib.game.utils.MessageUtil;
import lombok.Getter;
import org.mineacademy.fo.Common;

public final class Effects {

    @Getter
    private static EffectManager effectManager;


    public static void loadEffects() {
        if (Common.doesPluginExist("EffectLib"))
            effectManager = new EffectManager(DungeonPlugin.getInstance());
        else
            Common.log(MessageUtil.makeError("Could not load effects plugin. PLUGIN WILL NOT FUNCTION"));
    }

    public static void disable() {
        effectManager.dispose();
    }







}
