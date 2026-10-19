package org.notionsmp.bettercopycats;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(BetterCopycats.MODID)
public class BetterCopycats {
    public static final String MODID = "bettercopycats";

    public BetterCopycats() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
