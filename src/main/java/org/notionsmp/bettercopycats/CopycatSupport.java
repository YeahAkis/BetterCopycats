package org.notionsmp.bettercopycats;

import javax.annotation.Nullable;

import com.simibubi.create.content.decoration.copycat.CopycatBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
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

    public static double surfaceOffset(BlockGetter level, BlockPos pos) {
        if (!isCopycatsPlusLoaded()) {
            return 0.0D;
        }
        return CopycatsPlusCompat.surfaceOffset(level, pos);
    }

    public static boolean canBeLowered(BlockBehaviour.BlockStateBase state) {
        return state.getBlock() instanceof BushBlock && state.hasOffsetFunction();
    }

    public static double plantOffset(BlockGetter level, BlockPos pos, BlockBehaviour.BlockStateBase state) {
        if (!(state.getBlock() instanceof BushBlock)) {
            return 0.0D;
        }
        BlockPos soilPos = pos.below();
        if (state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)
                && state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER) {
            soilPos = soilPos.below();
        }
        double offset = surfaceOffset(level, soilPos);
        if (offset == 0.0D || !canBeLowered(state)) {
            return 0.0D;
        }
        return offset;
    }

    @Nullable
    public static BlockHitResult pickLoweredPlant(BlockGetter level, Vec3 from, Vec3 to, CollisionContext context, double maxDistanceSqr) {
        if (!isCopycatsPlusLoaded()) {
            return null;
        }
        BlockHitResult[] best = new BlockHitResult[1];
        double[] bestDistance = {maxDistanceSqr};
        BlockGetter.<Void, BlockGetter>traverseBlocks(from, to, level, (getter, cell) -> {
            BlockPos plantPos = cell.above().immutable();
            BlockState plant = getter.getBlockState(plantPos);
            if (plantOffset(getter, plantPos, plant) == 0.0D) {
                return null;
            }
            BlockHitResult hit = plant.getShape(getter, plantPos, context).clip(from, to, plantPos);
            if (hit != null) {
                double distance = hit.getLocation().distanceToSqr(from);
                if (distance < bestDistance[0]) {
                    bestDistance[0] = distance;
                    best[0] = hit;
                }
            }
            return null;
        }, getter -> null);
        return best[0];
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
