package org.notionsmp.bettercopycats.platform;

import net.neoforged.fml.ModList;

public final class Platform {
    private Platform() {
    }

    public static boolean isModListReady() {
        return ModList.get() != null;
    }

    public static boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }
}
