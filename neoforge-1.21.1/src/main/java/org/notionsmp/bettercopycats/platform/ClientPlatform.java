package org.notionsmp.bettercopycats.platform;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;

public final class ClientPlatform {
    private ClientPlatform() {
    }

    public static double blockReach(LocalPlayer player) {
        return player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);
    }
}
