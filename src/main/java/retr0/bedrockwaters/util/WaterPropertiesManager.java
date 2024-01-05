package retr0.bedrockwaters.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import retr0.bedrockwaters.config.BedrockWatersConfig;
import retr0.carrotconfig.config.ConfigSavedCallback;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static java.util.Map.entry;
import static net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags.*;
import static net.minecraft.registry.tag.BiomeTags.IS_HILL;
import static net.minecraft.registry.tag.BiomeTags.PRODUCES_CORALS_FROM_BONEMEAL;

/**
 * Utility for generating and handling custom water properties for biomes.
 */
@Environment(EnvType.CLIENT)
public final class WaterPropertiesManager {
    public static final BiomeProperties DEFAULT_VANILLA_PROPERTIES = new BiomeProperties("#3F76E4", "#050533", 15);
    public static final BiomeProperties DEFAULT_BEDROCK_PROPERTIES = new BiomeProperties("#44AFF5", "#44AFF5", 15);
    public static final int DEFAULT_WATER_FOG_DISTANCE = 15;

    /**
     * Field to hold mappings from biome registry keys to biome properties for vanilla biomes;
     */
    private static final Map<RegistryKey<Biome>, BiomeProperties> vanillaProperties =
        new HashMap<>(Map.<RegistryKey<Biome>, BiomeProperties>ofEntries(
            /* OCEAN BIOMES */
            entry(BiomeKeys.WARM_OCEAN,               new BiomeProperties("#02B0E5", "#0289D5", 60, 0.55f)),
            entry(BiomeKeys.LUKEWARM_OCEAN,           new BiomeProperties("#0D96DB", "#0A74C4", 60)),
            entry(BiomeKeys.DEEP_LUKEWARM_OCEAN,      new BiomeProperties("#0D96DB", "#0E72b9", 60)),
            entry(BiomeKeys.OCEAN,                    new BiomeProperties("#1787D4", "#1165b0", 60)),
            entry(BiomeKeys.DEEP_OCEAN,               new BiomeProperties("#1787D4", "#1463A5", 60)),
            entry(BiomeKeys.COLD_OCEAN,               new BiomeProperties("#2080C9", "#14559B", 60)),
            entry(BiomeKeys.DEEP_COLD_OCEAN,          new BiomeProperties("#2080C9", "#185390", 60)),
            entry(BiomeKeys.FROZEN_OCEAN,             new BiomeProperties("#2570B5", "#174985", 60)),
            entry(BiomeKeys.DEEP_FROZEN_OCEAN,        new BiomeProperties("#2570B5", "#1A4879", 60)),

            /* FOREST BIOMES */
            entry(BiomeKeys.FOREST,                   new BiomeProperties("#1E97F2", 60)),
            entry(BiomeKeys.FLOWER_FOREST,            new BiomeProperties("#20A3CC", 60)),
            entry(BiomeKeys.BIRCH_FOREST,             new BiomeProperties("#0677CE", 60)),
            entry(BiomeKeys.OLD_GROWTH_BIRCH_FOREST,  new BiomeProperties("#0677CE", 60)), // Unofficial fog distance!
            entry(BiomeKeys.DARK_FOREST,              new BiomeProperties("#3B6CD1", 60)),

            /* MOUNTAIN BIOMES */
            entry(BiomeKeys.MEADOW,                   new BiomeProperties("#0E4ECF")),
            entry(BiomeKeys.GROVE,                    new BiomeProperties("#0E4ECF")), // Unofficial!
            entry(BiomeKeys.SNOWY_SLOPES,             new BiomeProperties("#0E4ECF")), // Unofficial!
            entry(BiomeKeys.JAGGED_PEAKS,             new BiomeProperties("#0E4ECF")), // Unofficial!
            entry(BiomeKeys.FROZEN_PEAKS,             new BiomeProperties("#0E4ECF")), // Unofficial!
            entry(BiomeKeys.STONY_PEAKS,              new BiomeProperties("#0E4ECF")), // Unofficial!

            /* RIVER BIOMES */
            entry(BiomeKeys.RIVER,                    new BiomeProperties("#0084FF", 60)),
            entry(BiomeKeys.FROZEN_RIVER,             new BiomeProperties("#185390", 60)),

            /* BEACH BIOMES */
            entry(BiomeKeys.MUSHROOM_FIELDS,          new BiomeProperties("#8A8997")),
            entry(BiomeKeys.BEACH,                    new BiomeProperties("#157CAB", 60)),
            entry(BiomeKeys.STONY_SHORE,              new BiomeProperties("#0D67BB", 60)),
            entry(BiomeKeys.SNOWY_BEACH,              new BiomeProperties("#1463A5", 60)),

            /* TAIGA BIOMES */
            entry(BiomeKeys.TAIGA,                    new BiomeProperties("#287082", 60)),
            entry(BiomeKeys.SNOWY_TAIGA,              new BiomeProperties("#205E83", 60)),
            entry(BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA,  new BiomeProperties("#2D6D77", 60)),
            entry(BiomeKeys.OLD_GROWTH_PINE_TAIGA,    new BiomeProperties("#2D6D77", 60)),

            /* ICE BIOMES */
            entry(BiomeKeys.ICE_SPIKES,               new BiomeProperties("#14559B")),

            /* PLAINS BIOMES */
            entry(BiomeKeys.PLAINS,                   new BiomeProperties("#44AFF5", 60)),
            entry(BiomeKeys.SUNFLOWER_PLAINS,         new BiomeProperties("#44AFF5", 60)),
            entry(BiomeKeys.SNOWY_PLAINS,             new BiomeProperties("#14559B", 60)), // Unofficial fog distance!

            /* HILL BIOMES */
            entry(BiomeKeys.WINDSWEPT_HILLS,          new BiomeProperties("#007BF7")),
            entry(BiomeKeys.WINDSWEPT_GRAVELLY_HILLS, new BiomeProperties("#0E63AB")),
            entry(BiomeKeys.WINDSWEPT_FOREST,         new BiomeProperties("#0E63AB")),

            /* SWAMP BIOMES */
            entry(BiomeKeys.SWAMP,                    new BiomeProperties("#4C6559", "#4C6559", 30, 1.0f)),
            entry(BiomeKeys.MANGROVE_SWAMP,           new BiomeProperties("#3A7A6A", "#4D7A60", 30)),

            /* JUNGLE BIOMES */
            entry(BiomeKeys.JUNGLE,                   new BiomeProperties("#14A2C5", 60)),
            entry(BiomeKeys.SPARSE_JUNGLE,            new BiomeProperties("#0D8AE3")),
            entry(BiomeKeys.BAMBOO_JUNGLE,            new BiomeProperties("#14A2C5")),

            /* SAVANNA BIOMES */
            entry(BiomeKeys.SAVANNA,                  new BiomeProperties("#2C8B9C", 60)),
            entry(BiomeKeys.SAVANNA_PLATEAU,          new BiomeProperties("#2590A8")),
            entry(BiomeKeys.WINDSWEPT_SAVANNA,        new BiomeProperties("#2590A8")),

            /* BADLANDS BIOMES */
            entry(BiomeKeys.BADLANDS,                 new BiomeProperties("#4E7F81", 60)),
            entry(BiomeKeys.WOODED_BADLANDS,          new BiomeProperties("#497F99")),
            entry(BiomeKeys.ERODED_BADLANDS,          new BiomeProperties("#497F99")),

            /* DESERT BIOMES */
            entry(BiomeKeys.DESERT,                   new BiomeProperties("#32A598", 60)),

            /* CAVE BIOMES */
            entry(BiomeKeys.LUSH_CAVES,               new BiomeProperties("#14A2C5")), // Unofficial!
            entry(BiomeKeys.DRIPSTONE_CAVES,          new BiomeProperties("#32A598")), // Unofficial!
            entry(BiomeKeys.DEEP_DARK,                new BiomeProperties("#497F99")), // Unofficial!

            /* NETHER BIOMES */
            entry(BiomeKeys.NETHER_WASTES,            new BiomeProperties("#905957")),
            entry(BiomeKeys.SOUL_SAND_VALLEY,         new BiomeProperties("#905957")),
            entry(BiomeKeys.CRIMSON_FOREST,           new BiomeProperties("#905957")),
            entry(BiomeKeys.WARPED_FOREST,            new BiomeProperties("#905957")),
            entry(BiomeKeys.BASALT_DELTAS,            new BiomeProperties("#3F76E4", "#423E42", 15)),

            /* THE END BIOMES */
            entry(BiomeKeys.THE_END,                  new BiomeProperties("#62529E")),
            entry(BiomeKeys.SMALL_END_ISLANDS,        new BiomeProperties("#62529E")),
            entry(BiomeKeys.END_MIDLANDS,             new BiomeProperties("#62529E")),
            entry(BiomeKeys.END_HIGHLANDS,            new BiomeProperties("#62529E")),
            entry(BiomeKeys.END_BARRENS,              new BiomeProperties("#62529E"))
        ));

    /**
     * Cache for generated biome properties.
     */
    private static ConcurrentMap<Identifier, BiomeProperties> propertyCache = new ConcurrentHashMap<>();

    private static HashSet<Identifier> overwrittenBiomeIds = new HashSet<>();

    /**
     * Handler mappings to generate customized biome properties for unknown biomes.
     */
    private static final HandlerMap<TagKey<Biome>, RegistryEntry<Biome>, BiomeProperties> customizedProperties =
        new HandlerMap<>(Map.ofEntries(
            entry(OCEAN, b -> {
                if (b.isIn(AQUATIC_ICY))
                    return vanillaProperties.get(b.isIn(DEEP_OCEAN) ? BiomeKeys.DEEP_FROZEN_OCEAN : BiomeKeys.FROZEN_OCEAN);
                else if (b.isIn(PRODUCES_CORALS_FROM_BONEMEAL))
                    return vanillaProperties.get(b.isIn(DEEP_OCEAN) ? BiomeKeys.DEEP_LUKEWARM_OCEAN : BiomeKeys.WARM_OCEAN);
                else
                    return vanillaProperties.get(b.isIn(DEEP_OCEAN) ? BiomeKeys.DEEP_OCEAN : BiomeKeys.OCEAN);
            }),
            entry(FOREST, b -> {
                if (b.isIn(IS_HILL))
                    return vanillaProperties.get(BiomeKeys.WINDSWEPT_FOREST);
                else if (b.isIn(CLIMATE_COLD))
                    return vanillaProperties.get(BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA);
                else
                    return vanillaProperties.get(BiomeKeys.OLD_GROWTH_BIRCH_FOREST);
            }),
            entry(MOUNTAIN, b -> {
                if (b.isIn(ICY))
                    return vanillaProperties.get(BiomeKeys.FROZEN_PEAKS);
                if (b.isIn(MOUNTAIN_SLOPE))
                    return vanillaProperties.get(BiomeKeys.SNOWY_SLOPES);
                else if (b.isIn(MOUNTAIN_PEAK))
                    return vanillaProperties.get(BiomeKeys.JAGGED_PEAKS);
                else
                    return vanillaProperties.get(BiomeKeys.MEADOW);
            }),
            entry(RIVER,             b -> vanillaProperties.get(
                b.isIn(AQUATIC_ICY) ? BiomeKeys.FROZEN_RIVER : BiomeKeys.RIVER)),
            entry(BEACH,             b -> vanillaProperties.get(
                b.isIn(SNOWY) ? BiomeKeys.SNOWY_BEACH : BiomeKeys.BEACH)),
            entry(TAIGA,             b -> vanillaProperties.get(
                b.isIn(SNOWY) ? BiomeKeys.SNOWY_TAIGA : BiomeKeys.TAIGA)),
            entry(PLAINS,            b -> vanillaProperties.get(
                b.isIn(VEGETATION_DENSE) ? BiomeKeys.SUNFLOWER_PLAINS : BiomeKeys.PLAINS)),
            entry(EXTREME_HILLS,     b -> vanillaProperties.get(BiomeKeys.WINDSWEPT_HILLS)),
            entry(SWAMP,             b -> vanillaProperties.get(BiomeKeys.SWAMP)),
            entry(JUNGLE,            b -> vanillaProperties.get(BiomeKeys.JUNGLE)),
            entry(SAVANNA,           b -> vanillaProperties.get(
                b.isIn(VEGETATION_SPARSE) ? BiomeKeys.SAVANNA_PLATEAU : BiomeKeys.SAVANNA)),
            entry(BADLANDS,          b -> vanillaProperties.get(BiomeKeys.BADLANDS)),
            entry(DESERT,            b -> vanillaProperties.get(BiomeKeys.DESERT)),
            entry(CAVES,             b -> vanillaProperties.get(
                b.isIn(CLIMATE_WET) ? BiomeKeys.LUSH_CAVES : BiomeKeys.DRIPSTONE_CAVES)),
            entry(IN_NETHER,         b -> vanillaProperties.get(
                b.isIn(NETHER_FORESTS) ? BiomeKeys.CRIMSON_FOREST : BiomeKeys.BASALT_DELTAS)),
            entry(IN_THE_END,        b -> vanillaProperties.get(BiomeKeys.THE_END)),

            // For "themed biomes" we check for more generic tags.
            entry(ICY,               b -> vanillaProperties.get(BiomeKeys.ICE_SPIKES)),
            entry(SNOWY,             b -> vanillaProperties.get(BiomeKeys.SNOWY_PLAINS)),
            entry(FLORAL,            b -> vanillaProperties.get(BiomeKeys.FLOWER_FOREST)),
            entry(MUSHROOM,          b -> vanillaProperties.get(BiomeKeys.MUSHROOM_FIELDS)),

            // For all other cases, we check climate tags.
            entry(CLIMATE_DRY,       b -> vanillaProperties.get(BiomeKeys.DESERT)),
            entry(CLIMATE_HOT,       b -> vanillaProperties.get(BiomeKeys.JUNGLE)),
            entry(CLIMATE_TEMPERATE, b -> vanillaProperties.get(BiomeKeys.BIRCH_FOREST)),
            entry(CLIMATE_COLD,      b -> vanillaProperties.get(BiomeKeys.TAIGA))
        ), DEFAULT_BEDROCK_PROPERTIES);

    public static void init() {
        // Fill property cache with vanilla biome properties
        propertyCache = new ConcurrentHashMap<>();
        vanillaProperties.forEach((biomeKey, properties) -> propertyCache.put(biomeKey.getValue(), properties));

        // Load configured biome property overrides
        var biomeIds = new HashSet<String>();
        biomeIds.addAll(BedrockWatersConfig.waterColorOverrides.keySet());
        biomeIds.addAll(BedrockWatersConfig.waterFogDistanceOverrides.keySet());
        biomeIds.addAll(BedrockWatersConfig.waterOpacityOverrides.keySet());
        overwrittenBiomeIds = biomeIds.stream().map(Identifier::tryParse).collect(Collectors.toCollection(HashSet::new));

        // Register a new listener for the disconnection of the client play network handler. Whenever the client exits
        // a world, we regenerate the cache containing generated biome properties as it may contain data pack biomes.
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> init());
        ConfigSavedCallback.EVENT.register(configClass -> {
            init();
            var client = MinecraftClient.getInstance();
            // TODO: Find a way to make this not run every time any setting is changed!
            if (client.world != null) client.execute(client.worldRenderer::reload);
        });
    }


    /**
     * Gets the Bedrock Edition water properties for the target biome; or, if the biome is non-vanilla but has the
     * default vanilla water color, retrieves a cached customized water color dependent on the biome's tags.
     *
     * @param biomeKey The {@link RegistryKey<Biome>} associated with the target biome.
     * @param biomeRef The {@link RegistryEntry<Biome>} associated with the target biome.
     * @return A {@link BiomeProperties} reference containing the updated water properties.
     */
    public static BiomeProperties getWaterProperties(RegistryKey<Biome> biomeKey, RegistryEntry<Biome> biomeRef) {
        var biomeId = biomeKey.getValue();
        var properties = propertyCache.get(biomeId);
        var biome = biomeRef.value();

        // If the biome does yet have a cached property, generate properties and add them to the cache.
        if (properties == null) {
            properties = customizedProperties.handle(biomeRef, biomeRef::isIn);

            // For biomes which do not have default vanilla properties, we want to instead use their assigned water and
            // water fog colors but still keep the generated water fog distance (as it's a property not associated
            // with biome instances).
            if (!hasDefaultProperties(biome))
                properties = properties.override(biome.getWaterColor(), biome.getWaterFogColor(), null, null);
            if (overwrittenBiomeIds.contains(biomeId)) {
                var waterColor = BedrockWatersConfig.waterColorOverrides.getOrDefault(biomeId.toString(), null);
                var waterFogDistance = BedrockWatersConfig.waterFogDistanceOverrides.getOrDefault(biomeId.toString(), null);
                var waterOpacity = BedrockWatersConfig.waterOpacityOverrides.getOrDefault(biomeId.toString(), null);
                properties = properties.override(waterColor, waterColor, waterFogDistance, waterOpacity);
            }

            propertyCache.putIfAbsent(biomeId, properties);
        }

        return properties;
    }



    /**
     * @see WaterPropertiesManager#getWaterProperties(RegistryKey, RegistryEntry)
     */
    public static BiomeProperties getWaterProperties(RegistryEntry<Biome> biomeRef) {
        var biomeKey = biomeRef.getKey();

        return biomeKey.isPresent() ? getWaterProperties(biomeKey.get(), biomeRef) : DEFAULT_BEDROCK_PROPERTIES;
    }



    /**
     * Determines whether a biome has the default vanilla water and water fog colors.
     *
     * @param biome The target biome.
     * @return {@code true} if {@code biome} has the default vanilla water and water fog color; otherwise,
     * {@code false}.
     */
    public static boolean hasDefaultProperties(Biome biome) {
        return biome.getWaterColor() == DEFAULT_VANILLA_PROPERTIES.waterColor() &&
            biome.getWaterFogColor() == DEFAULT_VANILLA_PROPERTIES.waterFogColor();
    }
}
