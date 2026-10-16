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

    @Nullable
    private static CopycatSupport.Surface slabSurface(BlockGetter level, BlockPos pos, BlockState state) {
        if (state.getValue(CopycatSlabBlock.AXIS) != Direction.Axis.Y) {
            return null;
        }
        if (state.getValue(CopycatSlabBlock.SLAB_TYPE) == SlabType.BOTTOM) {
            return null;
        }
        BlockState material = IMultiStateCopycatBlock.getMaterial(level, pos, SlabType.TOP.getSerializedName());
        return new CopycatSupport.Surface(material, 0.0D);
    }
}
