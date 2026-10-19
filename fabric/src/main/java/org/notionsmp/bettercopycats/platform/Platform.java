package org.notionsmp.bettercopycats.platform;

import net.fabricmc.loader.api.FabricLoader;

public final class Platform {
    private Platform() {
    }

    public static boolean isModListReady() {
        return true;
    }

    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}
