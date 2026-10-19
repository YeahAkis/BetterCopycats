package org.notionsmp.bettercopycats.mixin.xaero;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.notionsmp.bettercopycats.CopycatSupport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "xaero.map.MapWriter")
public abstract class XaeroWorldMapWriterMixin {
    @Inject(method = "unpackFramedBlocks", at = @At("HEAD"), cancellable = true, require = 0)
    private void bettercopycats$unpackCopycats(BlockState state, Level level, BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
        BlockState material = CopycatSupport.mapMaterial(level, pos, state);
        if (material != null) {
            cir.setReturnValue(material);
        }
    }
}
