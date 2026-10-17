package org.notionsmp.bettercopycats;

import javax.annotation.Nullable;

import com.simibubi.create.content.decoration.copycat.CopycatBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.EmptyBlockGetter;
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

    public static boolean hasNoCollision(BlockBehaviour.BlockStateBase state) {
        return state.getCollisionShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO).isEmpty();
    }

    public static boolean canBeLowered(BlockBehaviour.BlockStateBase state) {
        return state.getBlock() instanceof BushBlock && (state.hasOffsetFunction() || hasNoCollision(state));
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

    public static void recheckPlantAbove(ServerLevel level, BlockPos pos, BlockState state) {
        if (!isCopycat(state.getBlock())) {
            return;
        }
        BlockPos above = pos.above();
        if (!level.isLoaded(above)) {
            return;
        }
        BlockState plant = level.getBlockState(above);
        if (plant.getBlock() instanceof BushBlock && !plant.canSurvive(level, above)) {
            level.destroyBlock(above, true);
        }
    }

    @Nullable
    public static Surface fullTopSurface(BlockGetter level, BlockPos pos, BlockState state, BlockState material) {
        if (!Block.isFaceFull(state.getCollisionShape(level, pos), Direction.UP)) {
            return null;
        }
        return new Surface(material, 0.0D);
    }

    private static boolean isCopycat(Block block) {
        if (block instanceof CopycatBlock) {
            return true;
        }
        return isCopycatsPlusLoaded() && CopycatsPlusCompat.isCopycat(block);
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
