package org.notionsmp.bettercopycats;

import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import fuzs.forgeconfigapiport.api.config.v2.ModConfigEvents;
import net.fabricmc.api.ModInitializer;
import net.minecraftforge.fml.config.ModConfig;

public class BetterCopycats implements ModInitializer {
    public static final String MODID = "bettercopycats";

    @Override
    public void onInitialize() {
        ModConfigEvents.loading(MODID).register(Config::onLoad);
        ModConfigEvents.reloading(MODID).register(Config::onLoad);
        ForgeConfigRegistry.INSTANCE.register(MODID, ModConfig.Type.COMMON, Config.SPEC);
    }
}
