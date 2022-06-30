package retr0.bedrockwaters;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import retr0.bedrockwaters.mixin.BiomeInvoker;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Long lost code: https://pastebin.com/iQyX4KRc
public class WaterPropertiesReplacer {
    //================================================================================
    // Biome Modification
    //================================================================================
    @SuppressWarnings("ConstantConditions")
    public static int getBiomeWaterProperties(RegistryEntry<Biome> targetBiome, boolean getWaterFogColor) {
        int waterColor;
        int waterFogColor;

        // Getting the target biome ID here so that WATER_COLOR in BiomeColors.class can be set through accessor.
        Identifier targetBiomeId              = MinecraftClient.getInstance().player.world
                                                .getRegistryManager().get(Registry.BIOME_KEY).getId(targetBiome.value());
        BiomeProperties targetBiomeProperties = BIOME_WATER_COLORS.get(targetBiomeId.toString());

        // If targetBiome has an entry.
        if (targetBiomeProperties != null)
        {
            waterColor    = hexStringToInt(targetBiomeProperties.waterColor);
            waterFogColor = hexStringToInt(targetBiomeProperties.waterFogColor);

            return getWaterFogColor ? waterFogColor : waterColor;
            //((IBiome)(Object) targetBiome).setWaterAttributes(waterColor, waterFogColor);
        }
        // Otherwise, if the biome is a modded biome and the biomes water color is the vanilla water color OR
        // the biome is a vanilla biome, automatically determine what the corresponding water color should be.
        else if ((!targetBiomeId.getNamespace().equals("minecraft") && targetBiome.value().getWaterColor() == 4159204) ||
                   targetBiomeId.getNamespace().equals("minecraft"))
        {
            waterColor    = hexStringToInt(getDefaultModifiedWaterAttributes(targetBiome, false));
            waterFogColor = hexStringToInt(getDefaultModifiedWaterAttributes(targetBiome, true));

            return getWaterFogColor ? waterFogColor : waterColor;
            //((IBiome)(Object) targetBiome).setWaterAttributes(waterColor, waterFogColor);
        }

        // If all else fails (though it shouldn't), use default colors.
        return 4501493;
    }



    private static int hexStringToInt(String hexString) {
        // Remove any extra formatting (yeah having this method is stupid).
        if (hexString.charAt(0) == '#')
            hexString = hexString.replace("#", "");
        else if (hexString.startsWith("0x"))
            hexString = hexString.replace("0x", "");
        return Integer.parseInt(hexString, 16);
    }



    private static String getDefaultModifiedWaterAttributes(RegistryEntry<Biome> targetBiome, boolean getWaterFog) {
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
        switch (((BiomeInvoker)(Object) targetBiome.value()).invokeGetCategory()) {
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
                if (targetBiome.value().getTemperature() >= 0.3f && targetBiome.value().getTemperature() < 0.9f)
                    return waterColorOf.apply("minecraft:plains");
            case FOREST:
                if (targetBiome.value().getTemperature() >= 0.3f && targetBiome.value().getTemperature() < 0.9f) {
                    if (biomeName.contains("dark"))
                        return waterColorOf.apply("minecraft:dark_forest");
                    if (varyWaterColor)
                        return biomeName.contains("hill") ? waterColorOf.apply("minecraft:forest") : waterColorOf.apply("minecraft:flower_forest");
                    return waterColorOf.apply("minecraft:old_growth_birch_forest");
                }

            /* Other special biome cases. */
            case MUSHROOM:
                return waterColorOf.apply("minecraft:mushroom_fields");
            case SWAMP:
                return waterColorOf.apply("minecraft:swamp");
            case JUNGLE:
                return waterColorOf.apply("minecraft:jungle");
            case SAVANNA:
                return biomeName.contains("plateau") ? waterColorOf.apply("minecraft:savanna_plateau") : waterColorOf.apply("minecraft:savanna");
            case MESA:
                if (varyWaterColor)
                    return waterColorOf.apply("minecraft:eroded_badlands");
                else if (biomeName.contains("plateau") || biomeName.contains("wooded"))
                    return waterColorOf.apply("minecraft:wooded_badlands");
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
            if (targetBiome.value().getTemperature() < 0.0f) {
                return waterColorOf.apply("minecraft:snowy_taiga");
            }
            else if (targetBiome.value().getTemperature() < 0.2f) {
                if (biomeName.contains("river") || biomeName.contains("lake"))
                    return waterColorOf.apply("minecraft:frozen_river");
                else if (biomeName.contains("beach") || biomeName.contains("shore"))
                    return waterColorOf.apply("minecraft:snowy_beach");
                return waterColorOf.apply("minecraft:snowy_plains");
            }
            else if (targetBiome.value().getTemperature() < 0.25f) {
                if (biomeName.contains("wood")) // FOR MOUNTAINOUS WOODED BIOMES
                    return waterColorOf.apply("minecraft:windswept_forest");
                if (biomeName.contains("beach") || biomeName.contains("shore"))
                    return waterColorOf.apply("minecraft:stony_shore");
                return waterColorOf.apply("minecraft:windswept_hills");
            }
            else if (targetBiome.value().getTemperature() < 0.3f) {
                if (biomeName.contains("mountain") || biomeName.contains("windswept"))
                    return waterColorOf.apply("minecraft:snowy_slopes");
                if (biomeName.contains("giant")) // FOR GIANT BIOMES
                    return waterColorOf.apply("minecraft:old_growth_spruce_taiga");
                return waterColorOf.apply("minecraft:taiga");
            }
            else if (targetBiome.value().getTemperature() < 0.5f) {
                return waterColorOf.apply("minecraft:old_growth_pine_taiga");
            }
            else if (targetBiome.value().getTemperature() < 0.6f) {
                return waterColorOf.apply("minecraft:river");
            }
            else if (targetBiome.value().getTemperature() < 0.7f) {
                return waterColorOf.apply("minecraft:birch_forest");
            }
            else if (targetBiome.value().getTemperature() < 0.8f) {
                return varyWaterColor ? waterColorOf.apply("minecraft:dark_forest") : waterColorOf.apply("minecraft:forest");
            }
            else if (targetBiome.value().getTemperature() < 0.95f) {
                return biomeName.contains("beach") || biomeName.contains("shore") ? waterColorOf.apply("minecraft:beach") : waterColorOf.apply("minecraft:plains");
            }
            else if (targetBiome.value().getTemperature() < 1.0f) {
                if (biomeName.contains("jungle") || biomeName.contains("rain"))
                    return waterColorOf.apply("minecraft:jungle");
                return waterColorOf.apply("minecraft:sparse_jungle");
            }
            else if (targetBiome.value().getTemperature() < 2.0f) {
                return biomeName.contains("hill") ? waterColorOf.apply("minecraft:savanna_plateau") : waterColorOf.apply("minecraft:savanna");
            }
            else if (targetBiome.value().getTemperature() >= 2.0f) {
                return waterColorOf.apply("minecraft:desert");
            }
        }

        // If all else fails, use default colors.
        return "#44AFF5";
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
        { "minecraft:windswept_hills",             new BiomeProperties("#007BF7") },
        { "minecraft:forest",                      new BiomeProperties("#1E97F2") },
        { "minecraft:taiga",                       new BiomeProperties("#287082") },
        { "minecraft:swamp",                       new BiomeProperties("#4c6559") },
        { "minecraft:river",                       new BiomeProperties("#0084FF") },
        { "minecraft:nether_wastes",               new BiomeProperties("#905957") },
        { "minecraft:the_end",                     new BiomeProperties("#62529E") },
        { "minecraft:frozen_ocean",                new BiomeProperties("#2570B5", "#174985") },
        { "minecraft:frozen_river",                new BiomeProperties("#185390") },
        { "minecraft:snowy_plains",                new BiomeProperties("#14559B") },
        //{ "minecraft:snowy_mountains",             new BiomeProperties("#1156a7") },
        { "minecraft:mushroom_fields",             new BiomeProperties("#8A8997") },
        //{ "minecraft:mushroom_field_shore",        new BiomeProperties("#818193") },
        { "minecraft:beach",                       new BiomeProperties("#157CAB") },
        //{ "minecraft:desert_hills",                new BiomeProperties("#1a7aa1") },
        //{ "minecraft:wooded_hills",                new BiomeProperties("#056bd1") },
        //{ "minecraft:taiga_hills",                 new BiomeProperties("#236583") },
        //{ "minecraft:mountain_edge",               new BiomeProperties("#045cd5") },
        { "minecraft:jungle",                      new BiomeProperties("#14A2C5") },
        //{ "minecraft:jungle_hills",                new BiomeProperties("#1B9ED8") },
        { "minecraft:sparse_jungle",               new BiomeProperties("#0D8AE3") },
        { "minecraft:deep_ocean",                  new BiomeProperties("#1787D4", "#1463A5") },
        { "minecraft:stony_shore",                 new BiomeProperties("#0D67BB") },
        { "minecraft:snowy_beach",                 new BiomeProperties("#1463A5") },
        { "minecraft:birch_forest",                new BiomeProperties("#0677CE") },
        { "minecraft:old_growth_birch_forest",     new BiomeProperties("#0677CE") },
        { "minecraft:dark_forest",                 new BiomeProperties("#3B6CD1") },
        { "minecraft:snowy_taiga",                 new BiomeProperties("#205E83") },
        //{ "minecraft:snowy_taiga_hills",           new BiomeProperties("#245b78") },
        { "minecraft:old_growth_pine_taiga",       new BiomeProperties("#2D6D77") },
        //{ "minecraft:giant_tree_taiga_hills",      new BiomeProperties("#2d6d77") },
        { "minecraft:windswept_forest",            new BiomeProperties("#0E63AB") },
        { "minecraft:savanna",                     new BiomeProperties("#2C8B9C") },
        { "minecraft:savanna_plateau",             new BiomeProperties("#2590A8") },
        { "minecraft:badlands",                    new BiomeProperties("#4E7F81") },
        { "minecraft:wooded_badlands",             new BiomeProperties("#497F99") },
        //{ "minecraft:badlands_plateau",            new BiomeProperties("#55809E") },

        /* THE END BIOMES */
        { "minecraft:small_end_islands",           new BiomeProperties("#62529E") },
        { "minecraft:end_midlands",                new BiomeProperties("#62529E") },
        { "minecraft:end_highlands",               new BiomeProperties("#62529E") },
        { "minecraft:end_barrens",                 new BiomeProperties("#62529E") },

        /* 1.13 OCEAN BIOMES */
        { "minecraft:warm_ocean",                  new BiomeProperties("#02B0E5", "#0289D5") },
        { "minecraft:lukewarm_ocean",              new BiomeProperties("#0D96DB", "#0A74C4") },
        { "minecraft:cold_ocean",                  new BiomeProperties("#2080C9", "#14559B") },
        { "minecraft:deep_warm_ocean",             new BiomeProperties("#02B0E5", "#0686CA") },
        { "minecraft:deep_lukewarm_ocean",         new BiomeProperties("#0D96DB", "#0E72b9") },
        { "minecraft:deep_cold_ocean",             new BiomeProperties("#2080C9", "#185390") },
        { "minecraft:deep_frozen_ocean",           new BiomeProperties("#2570B5", "#1A4879") },

        { "minecraft:sunflower_plains",            new BiomeProperties("#44AFF5") },
        { "minecraft:windswept_gravelly_hills",    new BiomeProperties("#0E63AB") },
        { "minecraft:flower_forest",               new BiomeProperties("#20A3CC") },
        //{ "minecraft:taiga_mountains",             new BiomeProperties("#1E6B82") },
        //{ "minecraft:swamp_hills",                 new BiomeProperties("#4c6156") },
        { "minecraft:ice_spikes",                  new BiomeProperties("#14559B") },
        //{ "minecraft:modified_jungle",             new BiomeProperties("#1B9ED8") },
        //{ "minecraft:modified_jungle_edge",        new BiomeProperties("#0D8AE3") },
        //{ "minecraft:snowy_taiga_mountains",       new BiomeProperties("#205e83") },
        { "minecraft:old_growth_spruce_taiga",     new BiomeProperties("#2D6D77") },
        //{ "minecraft:giant_spruce_taiga_hills",    new BiomeProperties("#286378") },
        //{ "minecraft:modified_gravelly_mountains", new BiomeProperties("#0E63AB") },
        { "minecraft:windswept_savanna",           new BiomeProperties("#2590A8") },
        { "minecraft:eroded_badlands",             new BiomeProperties("#497F99") },
        //{ "minecraft:modified_badlands_plateau",   new BiomeProperties("#55809E") },
        { "minecraft:bamboo_jungle",               new BiomeProperties("#14A2C5") },
        //{ "minecraft:bamboo_jungle_hills",         new BiomeProperties("#1B9ED8") },

        /* 1.18 MOUNTAIN BIOMES */
        { "minecraft:meadow",                      new BiomeProperties("#0E4ECF") },
        // these color values are unofficial!
        { "minecraft:grove",                       new BiomeProperties("#0E4ECF") },
        { "minecraft:snowy_slopes",                new BiomeProperties("#0E4ECF") },
        { "minecraft:jagged_peaks",                new BiomeProperties("#0E4ECF") },
        { "minecraft:frozen_peaks",                new BiomeProperties("#0E4ECF") },
        { "minecraft:stony_peaks",                 new BiomeProperties("#0E4ECF") },

        /* 1.18 CAVE BIOMES */
        // these color values are unofficial!
        { "minecraft:lush_caves",                  new BiomeProperties("#14A2C5") },
        { "minecraft:dripstone_caves",             new BiomeProperties("#32A598") },

        /* NETHER BIOMES */
        // these color values are unofficial!
        { "minecraft:soul_sand_valley",            new BiomeProperties("#968989") },
        { "minecraft:crimson_forest",              new BiomeProperties("#91211b") },
        { "minecraft:warped_forest",               new BiomeProperties("#512450") },
        { "minecraft:basalt_deltas",               new BiomeProperties("#474656") },
    }).collect(Collectors.toMap(data -> (String) data[0], data -> (BiomeProperties) data[1]));
}