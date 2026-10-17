package org.notionsmp.bettercopycats.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.notionsmp.bettercopycats.CopycatSupport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {
    @Inject(method = "sendBlockUpdated", at = @At("TAIL"))
    private void bettercopycats$recheckPlantAbove(BlockPos pos, BlockState oldState, BlockState newState, int flags, CallbackInfo ci) {
        if (oldState == newState) {
            CopycatSupport.recheckPlantAbove((ServerLevel) (Object) this, pos, newState);
        }
    }
}
