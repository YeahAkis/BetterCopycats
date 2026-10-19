package org.notionsmp.bettercopycats.mixin;

import net.minecraft.client.renderer.GameRenderer;
import org.notionsmp.bettercopycats.client.LoweredPlantPicker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Inject(method = "pick(F)V", at = @At("TAIL"))
    private void bettercopycats$pickLoweredPlants(float partialTicks, CallbackInfo ci) {
        LoweredPlantPicker.pick(partialTicks);
    }
}
