package retr0.bedrockwaters.util;

import net.minecraft.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

import java.util.HashMap;
import java.util.Map;

import static java.util.Map.entry;
import static retr0.bedrockwaters.util.WaterPropertiesManager.DEFAULT_WATER_FOG_DISTANCE;

/**
 * A record containing the water and water fog color for a biome. It can be initialized with Integer values or
 * String-based hexadecimal/octal values.
 *
 * @param waterColor The biome's water color.
 * @param waterFogColor The biome's water fog color.
 * @param waterFogDistance The biome's underwater fog distance.
 */
public record BiomeProperties(int waterColor, int waterFogColor, int waterFogDistance, float waterOpacity) {
    /**
     * Field to hold mappings from biome registry keys to biome properties for vanilla biomes.
     * @see <a href="https://minecraft.wiki/w/Water#Bedrock_Edition">Water#Bedrock Edition</a>
     */
    public static final Map<RegistryKey<Biome>, BiomeProperties> VANILLA_BIOME_PROPERTIES =
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
            entry(BiomeKeys.CHERRY_GROVE,             new BiomeProperties("#44AFF5")),

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
            entry(BiomeKeys.BASALT_DELTAS,            new BiomeProperties("#685F70", "#423E42", 15)), // Unofficial!

            /* THE END BIOMES */
            entry(BiomeKeys.THE_END,                  new BiomeProperties("#62529E")),
            entry(BiomeKeys.SMALL_END_ISLANDS,        new BiomeProperties("#62529E")),
            entry(BiomeKeys.END_MIDLANDS,             new BiomeProperties("#62529E")),
            entry(BiomeKeys.END_HIGHLANDS,            new BiomeProperties("#62529E")),
            entry(BiomeKeys.END_BARRENS,              new BiomeProperties("#62529E"))
        ));

    public BiomeProperties(String waterColor, String waterFogColor, int waterFogDistance, float waterOpacity) {
        this(Integer.decode(waterColor), Integer.decode(waterFogColor), waterFogDistance, waterOpacity);
    }

    public BiomeProperties(String waterColor, String waterFogColor, int waterFogDistance) {
        this(waterColor, waterFogColor, waterFogDistance, 0.65f);
    }

    public BiomeProperties(String waterColor, int waterFogDistance) { this(waterColor, waterColor, waterFogDistance); }

    public BiomeProperties(String waterColor) { this(waterColor, DEFAULT_WATER_FOG_DISTANCE); }

    // Practically, the distance the fog ends is ~1 block ahead of where it should be--we need to correct for that.
    public int waterFogDistance() { return waterFogDistance + 1; }

    public BiomeProperties override(Integer waterColor, Integer waterFogColor, Integer waterFogDistance, Float waterOpacity) {
        return new BiomeProperties(
                waterColor != null ? waterColor : this.waterColor,
                waterFogColor != null ? waterFogColor : this.waterFogColor,
                waterFogDistance != null ? waterFogDistance : this.waterFogDistance,
                waterOpacity != null ? waterOpacity : this.waterOpacity
        );
    }
}
