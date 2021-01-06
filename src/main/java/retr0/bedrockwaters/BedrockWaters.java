package retr0.bedrockwaters;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static retr0.bedrockwaters.WaterPropertiesReplacer.setBiomeWaterProperties;

public class BedrockWaters implements ModInitializer {
    public static Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "bedrockwaters";
    public static final String MOD_NAME = "BedrockWaters";

    private static final List<RegistryKey<Biome>> BIOMES;

    @Override
    public void onInitialize() {
        log(Level.INFO, "BedrockWaters initialized!");

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            // TODO: Lower water sprite opacity
            final Identifier bedrockWaterStillSpriteId = new Identifier(MOD_ID, "block/water_still");
            final Identifier bedrockWaterFlowSpriteId = new Identifier(MOD_ID, "block/water_flow");

            // add Bedrock Edition water sprites to the block atlas
            ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
                registry.register(bedrockWaterStillSpriteId);
                registry.register(bedrockWaterFlowSpriteId);
            });
        }

        try {
            Config.init(new File("./config/"+MOD_ID+"/"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        log(Level.INFO, "Modifying vanilla biome water attributes...");
        BIOMES.forEach(biomeKey -> {
            Biome currentBiome = BuiltinRegistries.BIOME.get(biomeKey.getValue());
            String properties = (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) ?
                    " properties -> WaterColor: #" + Integer.toHexString(currentBiome.getWaterColor()) +
                            ", WaterFogColor: #" + Integer.toHexString(currentBiome.getWaterFogColor()) : "";

            WaterPropertiesReplacer.setBiomeWaterProperties(currentBiome);
            log(Level.DEBUG, "Modified " + biomeKey.toString() + properties);
        });
        log(Level.INFO, "Finished!");

        // for modded biomes, we register a callback event which changes their water properties accordingly
        RegistryEntryAddedCallback.event(BuiltinRegistries.BIOME).register((i, identifier, biome) -> {
            String properties = (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) ?
                    " properties -> WaterColor: #" + Integer.toHexString(biome.getWaterColor()) +
                            ", WaterFogColor: #" + Integer.toHexString(biome.getWaterFogColor()) : "";

            setBiomeWaterProperties(biome);
            log(Level.DEBUG, "Modified " + identifier + properties);
        });
    }

    public static void log(Level level, String message) {
        LOGGER.log(level, "["+MOD_NAME+"] " + message);
    }

    // I have absolutely no idea where to find the full list of default biomes, so we're just gonna hard code it for now...
    static {
        BIOMES = ImmutableList.of(
            BiomeKeys.OCEAN,
            BiomeKeys.PLAINS,
            BiomeKeys.DESERT,
            BiomeKeys.MOUNTAINS,
            BiomeKeys.FOREST,
            BiomeKeys.TAIGA,
            BiomeKeys.SWAMP,
            BiomeKeys.RIVER,
            BiomeKeys.NETHER_WASTES,
            BiomeKeys.THE_END,
            BiomeKeys.FROZEN_OCEAN,
            BiomeKeys.FROZEN_RIVER,
            BiomeKeys.SNOWY_TUNDRA,
            BiomeKeys.SNOWY_MOUNTAINS,
            BiomeKeys.MUSHROOM_FIELDS,
            BiomeKeys.MUSHROOM_FIELD_SHORE,
            BiomeKeys.BEACH,
            BiomeKeys.DESERT_HILLS,
            BiomeKeys.WOODED_HILLS,
            BiomeKeys.TAIGA_HILLS,
            BiomeKeys.MOUNTAIN_EDGE,
            BiomeKeys.JUNGLE,
            BiomeKeys.JUNGLE_HILLS,
            BiomeKeys.JUNGLE_EDGE,
            BiomeKeys.DEEP_OCEAN,
            BiomeKeys.STONE_SHORE,
            BiomeKeys.SNOWY_BEACH,
            BiomeKeys.BIRCH_FOREST,
            BiomeKeys.BIRCH_FOREST_HILLS,
            BiomeKeys.DARK_FOREST,
            BiomeKeys.SNOWY_TAIGA,
            BiomeKeys.SNOWY_TAIGA_HILLS,
            BiomeKeys.GIANT_TREE_TAIGA,
            BiomeKeys.GIANT_TREE_TAIGA_HILLS,
            BiomeKeys.WOODED_MOUNTAINS,
            BiomeKeys.SAVANNA,
            BiomeKeys.SAVANNA_PLATEAU,
            BiomeKeys.BADLANDS,
            BiomeKeys.WOODED_BADLANDS_PLATEAU,
            BiomeKeys.BADLANDS_PLATEAU,
            BiomeKeys.SMALL_END_ISLANDS,
            BiomeKeys.END_MIDLANDS,
            BiomeKeys.END_HIGHLANDS,
            BiomeKeys.END_BARRENS,
            BiomeKeys.WARM_OCEAN,
            BiomeKeys.LUKEWARM_OCEAN,
            BiomeKeys.COLD_OCEAN,
            BiomeKeys.DEEP_WARM_OCEAN,
            BiomeKeys.DEEP_LUKEWARM_OCEAN,
            BiomeKeys.DEEP_COLD_OCEAN,
            BiomeKeys.DEEP_FROZEN_OCEAN,
            BiomeKeys.THE_VOID,
            BiomeKeys.SUNFLOWER_PLAINS,
            BiomeKeys.DESERT_LAKES,
            BiomeKeys.GRAVELLY_MOUNTAINS,
            BiomeKeys.FLOWER_FOREST,
            BiomeKeys.TAIGA_MOUNTAINS,
            BiomeKeys.SWAMP_HILLS,
            BiomeKeys.ICE_SPIKES,
            BiomeKeys.MODIFIED_JUNGLE,
            BiomeKeys.MODIFIED_JUNGLE_EDGE,
            BiomeKeys.TALL_BIRCH_FOREST,
            BiomeKeys.TALL_BIRCH_HILLS,
            BiomeKeys.DARK_FOREST_HILLS,
            BiomeKeys.SNOWY_TAIGA_MOUNTAINS,
            BiomeKeys.GIANT_SPRUCE_TAIGA,
            BiomeKeys.GIANT_SPRUCE_TAIGA_HILLS,
            BiomeKeys.MODIFIED_GRAVELLY_MOUNTAINS,
            BiomeKeys.SHATTERED_SAVANNA,
            BiomeKeys.SHATTERED_SAVANNA_PLATEAU,
            BiomeKeys.ERODED_BADLANDS,
            BiomeKeys.MODIFIED_WOODED_BADLANDS_PLATEAU,
            BiomeKeys.MODIFIED_BADLANDS_PLATEAU,
            BiomeKeys.BAMBOO_JUNGLE,
            BiomeKeys.BAMBOO_JUNGLE_HILLS,
            BiomeKeys.SOUL_SAND_VALLEY,
            BiomeKeys.CRIMSON_FOREST,
            BiomeKeys.WARPED_FOREST,
            BiomeKeys.BASALT_DELTAS
        );
    }
}
