package org.notionsmp.bettercopycats;

import javax.annotation.Nullable;

import com.simibubi.create.content.decoration.copycat.CopycatBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;
import org.notionsmp.bettercopycats.compat.CopycatsPlusCompat;

public final class CopycatSupport {
    private static final String COPYCATS_PLUS_ID = "copycats";
    private static Boolean copycatsPlusLoaded;

    private CopycatSupport() {
    }

    public record Surface(BlockState material, double yOffset) {
    }

    @Nullable
    public static Surface topSurface(BlockGetter level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof CopycatBlock) {
            return fullTopSurface(level, pos, state, CopycatBlock.getMaterial(level, pos));
        }
        if (isCopycatsPlusLoaded()) {
            return CopycatsPlusCompat.topSurface(level, pos, state);
        }
        return null;
    }

    @Nullable
    public static Surface fullTopSurface(BlockGetter level, BlockPos pos, BlockState state, BlockState material) {
        if (!Block.isFaceFull(state.getCollisionShape(level, pos), Direction.UP)) {
            return null;
        }
        return new Surface(material, 0.0D);
    }

    private static boolean isCopycatsPlusLoaded() {
        if (copycatsPlusLoaded == null) {
            ModList mods = ModList.get();
            if (mods == null) {
                return false;
            }
            copycatsPlusLoaded = mods.isLoaded(COPYCATS_PLUS_ID);
        }
        return copycatsPlusLoaded;
    }
}
