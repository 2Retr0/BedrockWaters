package retr0.bedrockwaters;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.biome.Biome;

public class WaterPropertiesReplacer {
    public static void setBiomeWaterProperties(Biome targetBiome) {
        int waterColor;
        int waterFogColor;

        Identifier targetBiomeId = BuiltinRegistries.BIOME.getId(targetBiome);
        Config.BiomeConfig biomeConfig = Config.getBiomeFromBiomeConfig(targetBiome);
        Config.BiomeConfig defaultConfig = Config.getDefaultBiomeConfig();

        // if biome has a corresponding BiomeConfig
        if (biomeConfig != null)
        {
            waterColor = hexStringToInt(biomeConfig.getBiomeProperties().getWaterColor());
            waterFogColor = hexStringToInt(biomeConfig.getBiomeProperties().getWaterFogColor());

            ((IBiome)(Object) targetBiome).setWaterAttributes(waterColor, waterFogColor);
        }
        // otherwise use default biome properties
        // (if the biome is modded, only change water properties if the water color is the vanilla water color)
        else if (defaultConfig != null
                && ((!targetBiomeId.getNamespace().equals("minecraft") && targetBiome.getWaterColor() == 4159204)
                    || (targetBiomeId.getNamespace().equals("minecraft"))))
        {
            waterColor = hexStringToInt(getDefaultModifiedWaterAttributes(targetBiome, false));
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
        String biomeName = targetBiome.toString().toLowerCase();
        boolean varyWaterColor = false;

        /* based on if the first character of the biome's name is in the first half of the alphabet (before 'n'),
         * we decide if we want to give it a varied water color. there is no other reason to do this besides giving
         * biome color per temperature a little extra variation
         */
        if (Character.getNumericValue(biomeName.charAt(0)) <= 109)
            varyWaterColor = true;

        //================================================================================
        // Special Biome Category Cases
        //================================================================================
        {
            /* check if biome is an ocean biome. because ocean biomes in bedrock edition have different water fog colors
             * compared to their water colors for both normal and deep variants, we need to check if we should return
             * a corresponding water color or water fog color. either way, this particular way of deciding biome water
             * properties for modded ocean biomes is terrible since checking for basic keywords is very weak--not that
             * I can think of a better way...
             */
            if (targetBiome.getCategory() == Biome.Category.OCEAN) {
                if (biomeName.contains("frozen")) {
                    if (getWaterFog)
                        // Frozen Deep Ocean biome : Frozen Ocean biome water fog color
                        return biomeName.contains("deep") ? "#1a4879" : "#174985";

                    // Frozen Ocean biome water color
                    return "#2570B5";
                } else if (biomeName.contains("cold")) {
                    if (getWaterFog)
                        // Cold Deep Ocean biome : Cold Ocean biome water fog color
                        return biomeName.contains("deep") ? "#185390" : "#14559b";

                    // Cold Ocean biome water color
                    return "#2080C9";
                } else if (biomeName.contains("lukewarm")) {
                    if (getWaterFog)
                        // Lukewarm Deep Ocean biome : Lukewarm Ocean biome water fog color
                        return biomeName.contains("deep") ? "#0e72b9" : "#0a74c4";

                    // Lukewarm Ocean biome water color
                    return "#0D96DB";
                } else if (biomeName.contains("warm")) {
                    if (getWaterFog)
                        // Warm Deep Ocean biome : Warm Ocean biome water fog color
                        return biomeName.contains("deep") ? "#0686ca" : "#0289d5";

                    // Warm Ocean biome water color
                    return "#02B0E5";
                } else {
                    if (getWaterFog)
                        // Deep Ocean biome : Ocean biome water fog color
                        return biomeName.contains("deep") ? "#0686ca" : "#0289d5";

                    // Ocean biome water color
                    return "#1787D4";
                }
            }

            // special cases for forest and plains biomes within a cold or temperate climate
            if ((targetBiome.getTemperature() >= 0.3f && targetBiome.getTemperature() < 0.9f)
                    && ((targetBiome.getCategory() == Biome.Category.FOREST) || (targetBiome.getCategory() == Biome.Category.PLAINS))) {
                if ((targetBiome.getCategory() == Biome.Category.FOREST)) {
                    if (biomeName.contains("dark"))
                        // Dark Forest biome water color
                        return "#3B6CD1";
                    if (varyWaterColor)
                        // Forest biome : Flower Forest biome water color
                        return biomeName.contains("hill") ? "#1E97F2" : "#20A3CC";
                    if (biomeName.contains("hill"))
                        // Wooded Hills biome water color
                        return "#056bd1";

                    // Birch Forest Hills biome water color
                    return "#0a74c4";
                }

                // Plains biome water color
                return "#44AFF5";
            }

            // special cases for mushroom biomes
            if (targetBiome.getCategory() == Biome.Category.MUSHROOM) {
                // Mushroom Fields Shore biome : Mushroom Fields biome water color
                return biomeName.contains("beach") || biomeName.contains("shore") ? "#818193" : "#8a8997";
            }

            // special cases for swamp biomes
            if (targetBiome.getCategory() == Biome.Category.SWAMP) {
                // Swamp Hills biome : Swamp biome water color
                return biomeName.contains("hill") ? "#4c6156" : "#4c6559";
            }

            // special cases for jungle biomes
            if (targetBiome.getCategory() == Biome.Category.JUNGLE) {
                // Jungle Hills biome : Jungle biome biome water color
                return biomeName.contains("hill") ? "#1B9ED8" : "#14A2C5";
            }

            // special cases for savanna biomes
            if (targetBiome.getCategory() == Biome.Category.SAVANNA) {
                // Savanna Plateau biome : Savanna biome water color
                return biomeName.contains("plateau") ? "#2590A8" : "#2C8B9C";
            }

            // special cases for badlands (mesa) biomes
            if (targetBiome.getCategory() == Biome.Category.MESA) {
                if (biomeName.contains("plateau") || varyWaterColor)
                    // Eroded Badlands biome : Badlands Plateau biome water color
                    return varyWaterColor ? "#497F99" : "#55809E";

                // Badlands biome water color
                return "#4E7F81";
            }

            // special cases for desert biomes
            if (targetBiome.getCategory() == Biome.Category.DESERT) {
                // Desert Hills biome : Desert biome water color
                return biomeName.contains("hill") ? "#1a7aa1" : "#32A598";
            }

            // special case for nether biomes
            if (targetBiome.getCategory() == Biome.Category.NETHER) {
                // "Warped Forest biome" : Nether Wastes biome water color
                return varyWaterColor ? "#512450" : "#905957";
            }

            // special case for end biomes
            if (targetBiome.getCategory() == Biome.Category.THEEND) {
                // The End biome water color
                return "#62529e";
            }
        }

        //================================================================================
        // Misc Biome Cases
        //================================================================================
        {
            // for all other cases...
            // (this should include biomes that belong in the TAIGA, BEACH, RIVER, EXTREME_HILLS, and ICY categories)
            if (targetBiome.getTemperature() < 0.0f) {
                // Snowy Taiga Hills : Snowy Taiga biome water color
                return biomeName.contains("hill") ? "#245b78" : "#205e83";
            } else if (targetBiome.getTemperature() >= 0.0f && targetBiome.getTemperature() < 0.2f) {
                if (biomeName.contains("river") || biomeName.contains("lake"))
                    // Frozen River biome water color
                    return "#185390";
                if (biomeName.contains("beach") || biomeName.contains("shore"))
                    // Snowy Beach biome water color
                    return "#1463a5";

                // Snowy Tundra biome water color
                return "#14559b";
            } else if (targetBiome.getTemperature() >= 0.2f && targetBiome.getTemperature() < 0.25f) {
                if (biomeName.contains("hill"))
                    // Wooded Hills biome water color
                    return "#056bd1";
                if (biomeName.contains("wood")) // FOR MOUNTAINOUS WOODED BIOMES
                    // Wooded Mountains biome water color
                    return "#0E63AB";
                if (biomeName.contains("beach") || biomeName.contains("shore"))
                    // Stone Shore biome water color
                    return "#0d67bb";

                // Mountains biome water color
                return "#007BF7";
            } else if (targetBiome.getTemperature() >= 0.25f && targetBiome.getTemperature() < 0.3f) {
                if (biomeName.contains("mountain"))
                    // Taiga Mountains biome water color
                    return "#1E6B82";
                if (biomeName.contains("hill"))
                    // Taiga Hills biome water color
                    return "#236583";
                if (biomeName.contains("giant")) // FOR GIANT BIOMES
                    // Giant Spruce Taiga biome water color
                    return "#2d6d77";

                // Taiga Biome water color
                return "#287082";
            } else if (targetBiome.getTemperature() >= 0.3f && targetBiome.getTemperature() < 0.5f) {
                // Giant Tree Taiga Hills biome : Giant Tree Taiga biome water color
                return biomeName.contains("hill") ? "#286378" : "#2d6d77";
            } else if (targetBiome.getTemperature() >= 0.5f && targetBiome.getTemperature() < 0.6f) {
                // River biome water color
                return "#0084FF";
            } else if (targetBiome.getTemperature() >= 0.6f && targetBiome.getTemperature() < 0.7f) {
                // Birch Forest Hills biome : Birch Forest biome water color
                return biomeName.contains("hill") ? "#0a74c4" : "#0677ce";
            } else if (targetBiome.getTemperature() >= 0.7f && targetBiome.getTemperature() < 0.8f) {
                // Dark Forest biome : Forest biome water color
                return varyWaterColor ? "#3B6CD1" : "#1E97F2";
            } else if (targetBiome.getTemperature() >= 0.8f && targetBiome.getTemperature() < 0.95f) {
                // Beach biome : Plains biome water color
                return biomeName.contains("beach") || biomeName.contains("shore") ? "#157cab" : "#44AFF5";
            } else if (targetBiome.getTemperature() >= 0.95f && targetBiome.getTemperature() < 1.0f) {
                if (biomeName.contains("jungle") || biomeName.contains("rain"))
                    // Jungle Hills : Jungle biome biome water color
                    return biomeName.contains("hill") ? "#1B9ED8" : "#14A2C5";

                // Jungle Edge biome water color
                return "#0D8AE3";
            } else if (targetBiome.getTemperature() >= 1.0f && targetBiome.getTemperature() < 2.0f) {
                // Savanna Plateau biome : Savanna biome water color
                return biomeName.contains("hill") ? "#2590A8" : "#2C8B9C";
            } else if (targetBiome.getTemperature() >= 2.0f) {
                // Desert Hills biome : Desert biome water color
                return biomeName.contains("hill") ? "#1a7aa1" : "#32A598";
            }
        }

        // if all else fails, use default colors
        return Config.getDefaultBiomeConfig().getBiomeProperties().getWaterColor();
    }
}