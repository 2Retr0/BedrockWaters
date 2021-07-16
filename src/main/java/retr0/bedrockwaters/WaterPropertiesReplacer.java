package retr0.bedrockwaters;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.biome.Biome;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Long lost code: https://pastebin.com/iQyX4KRc
@SuppressWarnings("ConstantConditions")
public class WaterPropertiesReplacer {
    //================================================================================
    // Biome Modification
    //================================================================================
    public static void setBiomeWaterProperties(Biome targetBiome) {
        int waterColor;
        int waterFogColor;

        Identifier targetBiomeId              = BuiltinRegistries.BIOME.getId(targetBiome);
        BiomeProperties targetBiomeProperties = BIOME_WATER_COLORS.get(targetBiomeId.toString());

        // If targetBiome has an entry.
        if (targetBiomeProperties != null)
        {
            waterColor    = hexStringToInt(targetBiomeProperties.waterColor);
            waterFogColor = hexStringToInt(targetBiomeProperties.waterFogColor);

            ((IBiome)(Object) targetBiome).setWaterAttributes(waterColor, waterFogColor);
        }
        // Otherwise, if the biome is a modded biome and the biomes water color is the vanilla water color OR
        // the biome is a vanilla biome, automatically determine what the corresponding water color should be.
        else if ((!targetBiomeId.getNamespace().equals("minecraft") && targetBiome.getWaterColor() == 4159204) ||
                   targetBiomeId.getNamespace().equals("minecraft"))
        {
            waterColor    = hexStringToInt(getDefaultModifiedWaterAttributes(targetBiome, false));
            waterFogColor = hexStringToInt(getDefaultModifiedWaterAttributes(targetBiome, true));

            ((IBiome)(Object) targetBiome).setWaterAttributes(waterColor, waterFogColor);
        }
    }



    private static int hexStringToInt(String hexString) {
        // remove any extra formatting
        if (hexString.charAt(0) == '#')
            hexString = hexString.replace("#", "");
        else if (hexString.startsWith("0x"))
            hexString = hexString.replace("0x", "");
        return Integer.parseInt(hexString, 16);
    }



    private static String getDefaultModifiedWaterAttributes(Biome targetBiome, boolean getWaterFog) {
        Function<String, String> waterColorOf    = (id) -> BIOME_WATER_COLORS.get(id).waterColor;
        Function<String, String> waterFogColorOf = (id) -> BIOME_WATER_COLORS.get(id).waterFogColor;

        String biomeName = targetBiome.toString().toLowerCase();

        /* Based on if the first character of the biome's name is in the first half of the alphabet (before 'n'),
         * we decide if we want to give it a varied water color. There is no other reason to do this besides giving
         * biome color per temperature a little extra variation
         */
        boolean varyWaterColor = Character.getNumericValue(biomeName.charAt(0)) <= 109;

        //================================================================================
        // Special Biome Category Cases
        //================================================================================
        switch (targetBiome.getCategory()) {
            /* Check if biome is an ocean biome. because ocean biomes in bedrock edition have different water fog colors
             * compared to their water colors for both normal and deep variants, we need to check if we should return
             * a corresponding water color or water fog color. Either way, this particular way of deciding biome water
             * properties for modded ocean biomes is terrible since checking for basic keywords is very weak--not that
             * I can think of a better way...
             */
            case OCEAN:
                if (biomeName.contains("frozen")) {
                    if (getWaterFog)
                        return biomeName.contains("deep") ? waterFogColorOf.apply("minecraft:deep_frozen_ocean") : waterFogColorOf.apply("minecraft:frozen_ocean");
                    return waterColorOf.apply("minecraft:frozen_ocean");
                }
                else if (biomeName.contains("cold")) {
                    if (getWaterFog)
                        return biomeName.contains("deep") ? waterFogColorOf.apply("minecraft:deep_cold_ocean") : waterFogColorOf.apply("minecraft:cold_ocean");
                    return waterColorOf.apply("minecraft:cold_ocean");
                }
                else if (biomeName.contains("lukewarm")) {
                    if (getWaterFog)
                        return biomeName.contains("deep") ? waterFogColorOf.apply("minecraft:deep_lukewarm_ocean") : waterFogColorOf.apply("minecraft:lukewarm_ocean");
                    return waterColorOf.apply("minecraft:lukewarm_ocean");
                }
                else if (biomeName.contains("warm")) {
                    if (getWaterFog)
                        return biomeName.contains("deep") ? waterFogColorOf.apply("minecraft:deep_warm_ocean") : waterFogColorOf.apply("minecraft:warm_ocean");
                    return waterColorOf.apply("minecraft:warm_ocean");
                }
                else {
                    if (getWaterFog)
                        return biomeName.contains("deep") ? waterFogColorOf.apply("minecraft:deep_ocean") : waterFogColorOf.apply("minecraft:ocean");
                    return waterColorOf.apply("minecraft:ocean");
                }

            /* Special cases for forest and plains biomes within a cold or temperate climate. */
            case PLAINS:
                if (targetBiome.getTemperature() >= 0.3f && targetBiome.getTemperature() < 0.9f)
                    return waterColorOf.apply("minecraft:plains");
            case FOREST:
                if (targetBiome.getTemperature() >= 0.3f && targetBiome.getTemperature() < 0.9f) {
                    if (biomeName.contains("dark"))
                        return waterColorOf.apply("minecraft:dark_forest");
                    if (varyWaterColor)
                        return biomeName.contains("hill") ? waterColorOf.apply("minecraft:forest") : waterColorOf.apply("minecraft:flower_forest");
                    if (biomeName.contains("hill"))
                        return waterColorOf.apply("minecraft:wooded_hills");
                    return waterColorOf.apply("minecraft:birch_forest_hills");
                }

            /* Other special biome cases. */
            case MUSHROOM:
                return biomeName.contains("beach") || biomeName.contains("shore") ? waterColorOf.apply("minecraft:mushroom_field_shore") : waterColorOf.apply("minecraft:mushroom_fields");
            case SWAMP:
                return biomeName.contains("hill") ? waterColorOf.apply("minecraft:swamp_hills") : waterColorOf.apply("minecraft:swamp");
            case JUNGLE:
                return biomeName.contains("hill") ? waterColorOf.apply("minecraft:jungle_hills") : waterColorOf.apply("minecraft:jungle");
            case SAVANNA:
                return biomeName.contains("plateau") ? waterColorOf.apply("minecraft:savanna_plateau") : waterColorOf.apply("minecraft:savanna");
            case MESA:
                if (varyWaterColor)
                    return waterColorOf.apply("minecraft:eroded_badlands");
                else if (biomeName.contains("plateau"))
                    return waterColorOf.apply("minecraft:badlands_plateau");
                return waterColorOf.apply("minecraft:badlands");
            case NETHER:
                return varyWaterColor ? waterColorOf.apply("minecraft:warped_forest") : waterColorOf.apply("minecraft:nether_wastes");
            case THEEND:
                return waterColorOf.apply("minecraft:the_end");
        }

        //================================================================================
        // Misc Biome Cases
        //================================================================================
        {
            // For all other cases...
            // (this should include biomes that belong in the TAIGA, BEACH, RIVER, EXTREME_HILLS, and ICY categories)
            if (targetBiome.getTemperature() < 0.0f) {
                return biomeName.contains("hill") ? waterColorOf.apply("minecraft:snowy_taiga_hills") : waterColorOf.apply("minecraft:snowy_taiga");
            }
            else if (targetBiome.getTemperature() < 0.2f) {
                if (biomeName.contains("river") || biomeName.contains("lake"))
                    return waterColorOf.apply("minecraft:frozen_river");
                else if (biomeName.contains("beach") || biomeName.contains("shore"))
                    return waterColorOf.apply("minecraft:snowy_beach");
                return waterColorOf.apply("minecraft:snowy_tundra");
            }
            else if (targetBiome.getTemperature() < 0.25f) {
                if (biomeName.contains("hill"))
                    return waterColorOf.apply("minecraft:wooded_hills");
                if (biomeName.contains("wood")) // FOR MOUNTAINOUS WOODED BIOMES
                    return waterColorOf.apply("minecraft:wooded_mountains");
                if (biomeName.contains("beach") || biomeName.contains("shore"))
                    return waterColorOf.apply("minecraft:stone_shore");
                return waterColorOf.apply("minecraft:mountains");
            }
            else if (targetBiome.getTemperature() < 0.3f) {
                if (biomeName.contains("mountain"))
                    return waterColorOf.apply("minecraft:taiga_mountains");
                if (biomeName.contains("hill"))
                    return waterColorOf.apply("minecraft:taiga_hills");
                if (biomeName.contains("giant")) // FOR GIANT BIOMES
                    return waterColorOf.apply("minecraft:giant_spruce_taiga");
                return waterColorOf.apply("minecraft:taiga");
            }
            else if (targetBiome.getTemperature() < 0.5f) {
                return biomeName.contains("hill") ? waterColorOf.apply("minecraft:giant_tree_taiga_hills") : waterColorOf.apply("minecraft:giant_tree_taiga");
            }
            else if (targetBiome.getTemperature() < 0.6f) {
                return waterColorOf.apply("minecraft:river");
            }
            else if (targetBiome.getTemperature() < 0.7f) {
                return biomeName.contains("hill") ? waterColorOf.apply("minecraft:birch_forest_hills") : waterColorOf.apply("minecraft:birch_forest");
            }
            else if (targetBiome.getTemperature() < 0.8f) {
                return varyWaterColor ? waterColorOf.apply("minecraft:dark_forest") : waterColorOf.apply("minecraft:forest");
            }
            else if (targetBiome.getTemperature() < 0.95f) {
                return biomeName.contains("beach") || biomeName.contains("shore") ? waterColorOf.apply("minecraft:beach") : waterColorOf.apply("minecraft:plains");
            }
            else if (targetBiome.getTemperature() < 1.0f) {
                if (biomeName.contains("jungle") || biomeName.contains("rain"))
                    return biomeName.contains("hill") ? waterColorOf.apply("minecraft:jungle_hills") : waterColorOf.apply("minecraft:jungle");
                return waterColorOf.apply("minecraft:jungle_edge");
            }
            else if (targetBiome.getTemperature() < 2.0f) {
                return biomeName.contains("hill") ? waterColorOf.apply("minecraft:savanna_plateau") : waterColorOf.apply("minecraft:savanna");
            }
            else if (targetBiome.getTemperature() >= 2.0f) {
                return biomeName.contains("hill") ? waterColorOf.apply("minecraft:desert_hills") : waterColorOf.apply("minecraft:desert");
            }
        }

        // If all else fails, use default colors.
        return "44AFF5";
    }



    //================================================================================
    // Biome Properties Data Structures
    //================================================================================
    public static class BiomeProperties {
        public final String waterColor;
        public final String waterFogColor;

        public BiomeProperties(String waterColor) {
            this.waterColor = waterColor;
            this.waterFogColor = waterColor;
        }

        public BiomeProperties(String waterColor, String waterFogColor) {
            this.waterColor = waterColor;
            this.waterFogColor = waterFogColor;
        }
    }

    public static final Map<String, BiomeProperties> BIOME_WATER_COLORS = Stream.of(new Object[][] {
        { "minecraft:ocean",                       new BiomeProperties("#1787D4", "#1165b0") },
        { "minecraft:plains",                      new BiomeProperties("#44AFF5") },
        { "minecraft:desert",                      new BiomeProperties("#32A598") },
        { "minecraft:mountains",                   new BiomeProperties("#007BF7") },
        { "minecraft:forest",                      new BiomeProperties("#1E97F2") },
        { "minecraft:taiga",                       new BiomeProperties("#287082") },
        { "minecraft:swamp",                       new BiomeProperties("#4c6559") },
        { "minecraft:river",                       new BiomeProperties("#0084FF") },
        { "minecraft:nether_wastes",               new BiomeProperties("#905957") },
        { "minecraft:the_end",                     new BiomeProperties("#62529e") },
        { "minecraft:frozen_ocean",                new BiomeProperties("#2570B5", "#174985") },
        { "minecraft:frozen_river",                new BiomeProperties("#185390") },
        { "minecraft:snowy_tundra",                new BiomeProperties("#14559b") },
        { "minecraft:snowy_mountains",             new BiomeProperties("#1156a7") },
        { "minecraft:mushroom_fields",             new BiomeProperties("#8a8997") },
        { "minecraft:mushroom_field_shore",        new BiomeProperties("#818193") },
        { "minecraft:beach",                       new BiomeProperties("#157cab") },
        { "minecraft:desert_hills",                new BiomeProperties("#1a7aa1") },
        { "minecraft:wooded_hills",                new BiomeProperties("#056bd1") },
        { "minecraft:taiga_hills",                 new BiomeProperties("#236583") },
        { "minecraft:mountain_edge",               new BiomeProperties("#045cd5") },
        { "minecraft:jungle",                      new BiomeProperties("#14A2C5") },
        { "minecraft:jungle_hills",                new BiomeProperties("#1B9ED8") },
        { "minecraft:jungle_edge",                 new BiomeProperties("#0D8AE3") },
        { "minecraft:deep_ocean",                  new BiomeProperties("#1787D4", "#1463a5") },
        { "minecraft:stone_shore",                 new BiomeProperties("#0d67bb") },
        { "minecraft:snowy_beach",                 new BiomeProperties("#1463a5") },
        { "minecraft:birch_forest",                new BiomeProperties("#0677ce") },
        { "minecraft:birch_forest_hills",          new BiomeProperties("#0a74c4") },
        { "minecraft:dark_forest",                 new BiomeProperties("#3B6CD1") },
        { "minecraft:snowy_taiga",                 new BiomeProperties("#205e83") },
        { "minecraft:snowy_taiga_hills",           new BiomeProperties("#245b78") },
        { "minecraft:giant_tree_taiga",            new BiomeProperties("#2d6d77") },
        { "minecraft:giant_tree_taiga_hills",      new BiomeProperties("#2d6d77") },
        { "minecraft:wooded_mountains",            new BiomeProperties("#0E63AB") },
        { "minecraft:savanna",                     new BiomeProperties("#2C8B9C") },
        { "minecraft:savanna_plateau",             new BiomeProperties("#2590A8") },
        { "minecraft:badlands",                    new BiomeProperties("#4E7F81") },
        { "minecraft:wooded_badlands_plateau",     new BiomeProperties("#497F99") },
        { "minecraft:badlands_plateau",            new BiomeProperties("#55809E") },

        /* THE END BIOMES */
        { "minecraft:small_end_islands",           new BiomeProperties("#62529e") },
        { "minecraft:end_midlands",                new BiomeProperties("#62529e") },
        { "minecraft:end_highlands",               new BiomeProperties("#62529e") },
        { "minecraft:end_barrens",                 new BiomeProperties("#62529e") },

        /* 1.13 OCEAN BIOMES */
        { "minecraft:warm_ocean",                  new BiomeProperties("#02B0E5", "#0289d5") },
        { "minecraft:lukewarm_ocean",              new BiomeProperties("#0D96DB", "#0a74c4") },
        { "minecraft:cold_ocean",                  new BiomeProperties("#2080C9", "#14559b") },
        { "minecraft:deep_warm_ocean",             new BiomeProperties("#02B0E5", "#0686ca") },
        { "minecraft:deep_lukewarm_ocean",         new BiomeProperties("#0D96DB", "#0e72b9") },
        { "minecraft:deep_cold_ocean",             new BiomeProperties("#2080C9", "#185390") },
        { "minecraft:deep_frozen_ocean",           new BiomeProperties("#2570B5", "#1a4879") },

        { "minecraft:sunflower_plains",            new BiomeProperties("#44AFF5") },
        { "minecraft:gravelly_mountains",          new BiomeProperties("#0E63AB") },
        { "minecraft:flower_forest",               new BiomeProperties("#20A3CC") },
        { "minecraft:taiga_mountains",             new BiomeProperties("#1E6B82") },
        { "minecraft:swamp_hills",                 new BiomeProperties("#4c6156") },
        { "minecraft:ice_spikes",                  new BiomeProperties("#14559b") },
        { "minecraft:modified_jungle",             new BiomeProperties("#1B9ED8") },
        { "minecraft:modified_jungle_edge",        new BiomeProperties("#0D8AE3") },
        { "minecraft:snowy_taiga_mountains",       new BiomeProperties("#205e83") },
        { "minecraft:giant_spruce_taiga",          new BiomeProperties("#2d6d77") },
        { "minecraft:giant_spruce_taiga_hills",    new BiomeProperties("#286378") },
        { "minecraft:modified_gravelly_mountains", new BiomeProperties("#0E63AB") },
        { "minecraft:shattered_savanna",           new BiomeProperties("#2590A8") },
        { "minecraft:eroded_badlands",             new BiomeProperties("#497F99") },
        { "minecraft:modified_badlands_plateau",   new BiomeProperties("#55809E") },
        { "minecraft:bamboo_jungle",               new BiomeProperties("#14A2C5") },
        { "minecraft:bamboo_jungle_hills",         new BiomeProperties("#1B9ED8") },

        /* NETHER BIOMES */
        // these color values are unofficial!
        { "minecraft:soul_sand_valley",            new BiomeProperties("#968989") },
        { "minecraft:crimson_forest",              new BiomeProperties("#91211b") },
        { "minecraft:warped_forest",               new BiomeProperties("#512450") },
        { "minecraft:basalt_deltas",               new BiomeProperties("#474656") },
    }).collect(Collectors.toMap(data -> (String) data[0], data -> (BiomeProperties) data[1]));
}