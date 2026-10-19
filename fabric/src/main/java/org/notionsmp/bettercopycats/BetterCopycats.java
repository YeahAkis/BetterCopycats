package org.notionsmp.bettercopycats;

import net.fabricmc.api.ModInitializer;
import net.minecraftforge.api.ModLoadingContext;
import net.minecraftforge.api.fml.event.config.ModConfigEvents;
import net.minecraftforge.fml.config.ModConfig;

public class BetterCopycats implements ModInitializer {
    public static final String MODID = "bettercopycats";

    @Override
    public void onInitialize() {
        ModConfigEvents.loading(MODID).register(Config::onLoad);
        ModConfigEvents.reloading(MODID).register(Config::onLoad);
        ModLoadingContext.registerConfig(MODID, ModConfig.Type.COMMON, Config.SPEC);
    }
}
