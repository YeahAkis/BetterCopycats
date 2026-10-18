package org.notionsmp.bettercopycats;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(BetterCopycats.MODID)
public class BetterCopycats {
    public static final String MODID = "bettercopycats";

    public BetterCopycats(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
