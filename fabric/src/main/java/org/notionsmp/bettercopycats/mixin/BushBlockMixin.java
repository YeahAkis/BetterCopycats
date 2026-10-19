package org.notionsmp.bettercopycats.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.notionsmp.bettercopycats.CopycatSupport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BushBlock.class)
public abstract class BushBlockMixin {
    @Shadow
    protected abstract boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos);

    @Inject(method = "canSurvive", at = @At("HEAD"), cancellable = true)
    private void bettercopycats$surviveOnCopycat(BlockState state, LevelReader level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        BlockPos soilPos = pos.below();
        CopycatSupport.Surface surface = CopycatSupport.topSurface(level, soilPos);
        if (surface == null) {
            return;
        }
        if (!CopycatSupport.isPlantAllowed(state)) {
            return;
        }
        if (surface.yOffset() != 0.0D && !CopycatSupport.canBeLowered(state)) {
            return;
        }
        BlockState soil = surface.material();
        boolean supported = this.mayPlaceOn(soil, level, soilPos);
        if (supported) {
            cir.setReturnValue(true);
        }
    }
}
