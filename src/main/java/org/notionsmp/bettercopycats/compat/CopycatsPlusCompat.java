package org.notionsmp.bettercopycats.compat;

import javax.annotation.Nullable;

import com.copycatsplus.copycats.content.copycat.slab.CopycatSlabBlock;
import com.copycatsplus.copycats.foundation.copycat.ICopycatBlock;
import com.copycatsplus.copycats.foundation.copycat.multistate.IMultiStateCopycatBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.notionsmp.bettercopycats.CopycatSupport;

public final class CopycatsPlusCompat {
    private static final double BOTTOM_SLAB_OFFSET = -0.5D;

    private CopycatsPlusCompat() {
    }

    @Nullable
    public static CopycatSupport.Surface topSurface(BlockGetter level, BlockPos pos, BlockState state) {
        Block block = state.getBlock();
        if (block instanceof CopycatSlabBlock) {
            return slabSurface(level, pos, state);
        }
        if (block instanceof IMultiStateCopycatBlock) {
            return null;
        }
        if (block instanceof ICopycatBlock) {
            return CopycatSupport.fullTopSurface(level, pos, state, ICopycatBlock.getMaterial(level, pos));
        }
        return null;
    }

    public static boolean isCopycat(Block block) {
        return block instanceof ICopycatBlock;
    }

    public static double surfaceOffset(BlockGetter level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof CopycatSlabBlock)) {
            return 0.0D;
        }
        if (state.getValue(CopycatSlabBlock.AXIS) != Direction.Axis.Y) {
            return 0.0D;
        }
        return state.getValue(CopycatSlabBlock.SLAB_TYPE) == SlabType.BOTTOM ? BOTTOM_SLAB_OFFSET : 0.0D;
    }

    @Nullable
    private static CopycatSupport.Surface slabSurface(BlockGetter level, BlockPos pos, BlockState state) {
        if (state.getValue(CopycatSlabBlock.AXIS) != Direction.Axis.Y) {
            return null;
        }
        boolean bottomOnly = state.getValue(CopycatSlabBlock.SLAB_TYPE) == SlabType.BOTTOM;
        String part = (bottomOnly ? SlabType.BOTTOM : SlabType.TOP).getSerializedName();
        BlockState material = IMultiStateCopycatBlock.getMaterial(level, pos, part);
        return new CopycatSupport.Surface(material, bottomOnly ? BOTTOM_SLAB_OFFSET : 0.0D);
    }
}
