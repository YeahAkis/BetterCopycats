package org.notionsmp.bettercopycats.platform;

import net.minecraft.client.player.LocalPlayer;

public final class ClientPlatform {
    private ClientPlatform() {
    }

    public static double blockReach(LocalPlayer player) {
        return player.getBlockReach();
    }
}
