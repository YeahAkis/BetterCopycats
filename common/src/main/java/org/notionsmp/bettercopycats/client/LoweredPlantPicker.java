package org.notionsmp.bettercopycats.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.notionsmp.bettercopycats.CopycatSupport;
import org.notionsmp.bettercopycats.platform.ClientPlatform;

public final class LoweredPlantPicker {
    private LoweredPlantPicker() {
    }

    public static void pick(float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        if (!(mc.cameraEntity instanceof LocalPlayer player) || mc.level == null) {
            return;
        }
        Vec3 from = player.getEyePosition(partialTicks);
        double range = ClientPlatform.blockReach(player);
        Vec3 to = from.add(player.getViewVector(partialTicks).scale(range));
        HitResult current = mc.hitResult;
        double currentDistance = current == null || current.getType() == HitResult.Type.MISS
                ? Double.MAX_VALUE
                : current.getLocation().distanceToSqr(from);
        BlockHitResult hit = CopycatSupport.pickLoweredPlant(mc.level, from, to, CollisionContext.of(player), currentDistance);
        if (hit == null) {
            return;
        }
        Vec3 center = Vec3.atCenterOf(hit.getBlockPos());
        Vec3 relative = hit.getLocation().subtract(center);
        Vec3 clamped = new Vec3(Mth.clamp(relative.x, -1.0D, 1.0D), Mth.clamp(relative.y, -1.0D, 1.0D), Mth.clamp(relative.z, -1.0D, 1.0D));
        mc.hitResult = new BlockHitResult(clamped.add(center), hit.getDirection(), hit.getBlockPos(), hit.isInside());
    }
}
