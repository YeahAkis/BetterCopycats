package org.notionsmp.bettercopycats.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.notionsmp.bettercopycats.CopycatSupport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin {
    @Inject(method = "getOffset", at = @At("RETURN"), cancellable = true)
    private void bettercopycats$lowerOntoBottomSlab(BlockGetter level, BlockPos pos, CallbackInfoReturnable<Vec3> cir) {
        BlockBehaviour.BlockStateBase self = (BlockBehaviour.BlockStateBase) (Object) this;
        double offset = CopycatSupport.plantOffset(level, pos, self);
        if (offset != 0.0D) {
            cir.setReturnValue(cir.getReturnValue().add(0.0D, offset, 0.0D));
        }
    }

    @Inject(method = "getOffsetType", at = @At("RETURN"), cancellable = true)
    private void bettercopycats$plantsHaveOffset(CallbackInfoReturnable<BlockBehaviour.OffsetType> cir) {
        BlockBehaviour.BlockStateBase self = (BlockBehaviour.BlockStateBase) (Object) this;
        if (cir.getReturnValue() == BlockBehaviour.OffsetType.NONE && self.getBlock() instanceof BushBlock && CopycatSupport.hasNoCollision(self)) {
            cir.setReturnValue(BlockBehaviour.OffsetType.XZ);
        }
    }

    @Inject(method = "getShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;",
            at = @At("RETURN"), cancellable = true)
    private void bettercopycats$lowerShape(BlockGetter level, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        BlockBehaviour.BlockStateBase self = (BlockBehaviour.BlockStateBase) (Object) this;
        if (!(self.getBlock() instanceof BushBlock)) {
            return;
        }
        double offset = CopycatSupport.plantOffset(level, pos, self);
        if (offset == 0.0D) {
            return;
        }
        VoxelShape shape = cir.getReturnValue();
        if (shape.isEmpty() || shape.min(Direction.Axis.Y) <= offset + 1.0E-4D) {
            return;
        }
        cir.setReturnValue(shape.move(0.0D, offset, 0.0D));
    }

    @Inject(method = "getMapColor", at = @At("RETURN"), cancellable = true)
    private void bettercopycats$useSkinMapColor(BlockGetter level, BlockPos pos, CallbackInfoReturnable<MaterialColor> cir) {
        BlockState self = (BlockState) (Object) this;
        MaterialColor color = CopycatSupport.mapColor(level, pos, self);
        if (color != null) {
            cir.setReturnValue(color);
        }
    }
}
