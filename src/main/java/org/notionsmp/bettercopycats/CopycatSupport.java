package org.notionsmp.bettercopycats;

import javax.annotation.Nullable;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.decoration.copycat.CopycatBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.StemBlock;
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
    private static final String FRAMED_BLOCKS_ID = "framedblocks";
    private static Boolean copycatsPlusLoaded;
    private static Boolean framedBlocksLoaded;

    private CopycatSupport() {
    }

    public record Surface(BlockState material, double yOffset) {
    }

    @Nullable
    public static Surface topSurface(BlockGetter level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        Surface surface = null;
        if (state.getBlock() instanceof CopycatBlock) {
            surface = fullTopSurface(level, pos, state, CopycatBlock.getMaterial(level, pos));
        } else if (isCopycatsPlusLoaded()) {
            surface = CopycatsPlusCompat.topSurface(level, pos, state);
        }
        if (surface != null && surface.yOffset() != 0.0D && !Config.lowerOnBottomSlabs()) {
            return null;
        }
        return surface;
    }

    public static double surfaceOffset(BlockGetter level, BlockPos pos) {
        if (!isCopycatsPlusLoaded() || !Config.lowerOnBottomSlabs()) {
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

    public static boolean isPlantAllowed(BlockState plant) {
        Block block = plant.getBlock();
        if (block instanceof SaplingBlock && !Config.allowSaplings()) {
            return false;
        }
        boolean crop = block instanceof CropBlock || block instanceof StemBlock || block instanceof NetherWartBlock;
        if (crop && !Config.allowCrops()) {
            return false;
        }
        return !Config.isBlacklisted(block);
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
    public static BlockState mapMaterial(BlockGetter level, BlockPos pos, BlockState state) {
        if (!Config.mapSupport()) {
            return null;
        }
        BlockState material;
        if (state.getBlock() instanceof CopycatBlock) {
            material = CopycatBlock.getMaterial(level, pos);
        } else if (isCopycatsPlusLoaded()) {
            material = CopycatsPlusCompat.mapMaterial(level, pos, state);
        } else {
            return null;
        }
        if (material == null || material.isAir() || AllBlocks.COPYCAT_BASE.has(material)) {
            return null;
        }
        return material;
    }

    public static boolean isFramedBlocksLoaded() {
        if (framedBlocksLoaded == null) {
            ModList mods = ModList.get();
            if (mods == null) {
                return false;
            }
            framedBlocksLoaded = mods.isLoaded(FRAMED_BLOCKS_ID);
        }
        return framedBlocksLoaded;
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
