package org.notionsmp.bettercopycats;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = BetterCopycats.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue LOWER_ON_BOTTOM_SLABS = BUILDER
            .comment("Place plants half a block lower on bottom copycat slabs. When off, plants cannot be placed on bottom slabs at all")
            .define("lowerPlantsOnBottomSlabs", true);

    private static final ModConfigSpec.BooleanValue ALLOW_SAPLINGS = BUILDER
            .comment("Allow saplings on copycats. Growing trees replace the block below with dirt, which would destroy the copycat")
            .define("allowSaplings", false);

    private static final ModConfigSpec.BooleanValue ALLOW_CROPS = BUILDER
            .comment("Allow crops, stems and nether wart on copycats. They grow at the slowest rate because the block below is not farmland")
            .define("allowCrops", false);

    private static final ModConfigSpec.ConfigValue<List<? extends String>> PLANT_BLACKLIST = BUILDER
            .comment("Block ids of plants that can never be placed on copycats, for example minecraft:poppy")
            .defineListAllowEmpty("plantBlacklist", List.of(), Config::isBlockId);

    private static final ModConfigSpec.BooleanValue MAP_SUPPORT = BUILDER
            .comment("Show the skin of copycats on maps. Vanilla maps are handled by the server, Xaero's Minimap and World Map by the client")
            .define("mapSupport", true);

    static final ModConfigSpec SPEC = BUILDER.build();

    private static volatile boolean lowerOnBottomSlabs = true;
    private static volatile boolean allowSaplings = false;
    private static volatile boolean allowCrops = false;
    private static volatile boolean mapSupport = true;
    private static volatile Set<ResourceLocation> plantBlacklist = Set.of();

    private Config() {
    }

    public static boolean lowerOnBottomSlabs() {
        return lowerOnBottomSlabs;
    }

    public static boolean allowSaplings() {
        return allowSaplings;
    }

    public static boolean allowCrops() {
        return allowCrops;
    }

    public static boolean mapSupport() {
        return mapSupport;
    }

    public static boolean isBlacklisted(Block block) {
        return plantBlacklist.contains(BuiltInRegistries.BLOCK.getKey(block));
    }

    private static boolean isBlockId(Object value) {
        return value instanceof String id && ResourceLocation.tryParse(id) != null;
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        lowerOnBottomSlabs = LOWER_ON_BOTTOM_SLABS.get();
        allowSaplings = ALLOW_SAPLINGS.get();
        allowCrops = ALLOW_CROPS.get();
        mapSupport = MAP_SUPPORT.get();

        Set<ResourceLocation> ids = new HashSet<>();
        for (String id : PLANT_BLACKLIST.get()) {
            ResourceLocation key = ResourceLocation.tryParse(id);
            if (key != null) {
                ids.add(key);
            }
        }
        plantBlacklist = Set.copyOf(ids);
    }
}
