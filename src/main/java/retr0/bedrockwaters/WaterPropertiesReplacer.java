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
        Config.BiomeConfig parentConfig = null;
        Config.BiomeConfig defaultConfig = Config.getDefaultBiomeConfig();

        // only set parentConfig if targetBiome has a parent as Identifier.tryParse() will throw a NullPointerException
        /*if (targetBiome.hasParent())
            parentConfig = Config.getBiomeFromBiomeConfig(BuiltinRegistries.BIOME.get(Identifier.tryParse(targetBiome.getParent())));*/

        // if biome has a corresponding BiomeConfig
        if (biomeConfig != null)
        {
            waterColor = hexStringToInt(biomeConfig.getBiomeProperties().getWaterColor());
            waterFogColor = hexStringToInt(biomeConfig.getBiomeProperties().getWaterFogColor());

            ((BiomeExt)(Object)targetBiome).setWaterAttributes(waterColor, waterFogColor);
        }
        // otherwise if the biome has a parent, use parent biome properties
        /*else if ((targetBiomeId.getNamespace().equals("minecraft") || !Config.allowVanillaBiomesOnly())
                && Config.canChildBiomeInheritParentColors()
                && parentConfig != null)
        {
            waterColor = hexStringToInt(parentConfig.getBiomeProperties().getWaterColor());
            waterFogColor = hexStringToInt(parentConfig.getBiomeProperties().getWaterFogColor());

            ((BiomeExt)targetBiome).setWaterAttributes(waterColor, waterFogColor);
        }*/
        // otherwise use default biome properties
        // (if the biome is modded, only change water properties if the water color is the vanilla water color)
        else if (defaultConfig != null
                && ((!targetBiomeId.getNamespace().equals("minecraft") && targetBiome.getWaterColor() == 4159204)
                    || (targetBiomeId.getNamespace().equals("minecraft"))))
        {
            waterColor = hexStringToInt(getDefaultModifiedWaterAttributes(targetBiome, false));
            waterFogColor = hexStringToInt(getDefaultModifiedWaterAttributes(targetBiome, true));

            ((BiomeExt)(Object)targetBiome).setWaterAttributes(waterColor, waterFogColor);
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

        System.out.println(biomeName);
        if (biomeName.contains("badland") && biomeName.contains("plateau")) System.out.println(targetBiome.getTemperature());

        /* based on if the first character of the biome's name is in the first half of the alphabet (before 'n'),
         * we decide if we want to give it a varied water color. there is no other reason to do this besides giving
         * biome color per temperature a little extra variation
         */
        if (Character.getNumericValue(biomeName.charAt(0)) <= 109)
            varyWaterColor = true;

        // check if biome is a nether biome
        /*if (InternalBiomeData.getNetherBiomes().contains(targetBiome)) {
            if (varyWaterColor) {
                // UNOFFICIAL Warped Forest biome water color
                return "#512450";
            } else {
                // Nether Wastes biome water color
                return "#905957";
            }
        }*/

        /* check if biome is an ocean biome. because ocean biomes in bedrock edition have different water fog colors
         * compared to their water colors for both normal and deep variants, we need to check if we should return
         * a corresponding water color or water fog color. either way, this particular way of deciding biome water
         * properties for modded ocean biomes is terrible since checking for basic keywords is very weak--not that
         * I can think of a better way...
         */
        if (biomeName.contains("ocean")) {
            if (biomeName.contains("frozen")) {
                    if (getWaterFog) {
                        if (biomeName.contains("deep"))
                            // Frozen Deep Ocean biome water fog color
                            return "#1a4879";

                        // Frozen Ocean biome water fog color
                        return "#174985";
                    }

                    // Frozen Ocean biome water color
                    return "#2570B5";
            }
            else if (biomeName.contains("cold")) {
                    if (getWaterFog) {
                        if (biomeName.contains("deep"))
                            // Cold Deep Ocean biome water fog color
                            return "#185390";

                        // Cold Ocean biome water fog color
                        return "#14559b";
                    }

                    // Cold Ocean biome water color
                    return "#2080C9";
            }
            else if (biomeName.contains("lukewarm")) {
                    if (getWaterFog) {
                        if (biomeName.contains("deep"))
                            // Lukewarm Deep Ocean biome water fog color
                            return "#0e72b9";

                        // Lukewarm Ocean biome water fog color
                        return "#0a74c4";
                    }

                    // Lukewarm Ocean biome water color
                    return "#0D96DB";
            }
            else if (biomeName.contains("warm")) {
                    if (getWaterFog) {
                        if (biomeName.contains("deep"))
                            // Warm Deep Ocean biome water fog color
                            return "#0686ca";

                        // Warm Ocean biome water fog color
                        return "#0289d5";
                    }

                    // Warm Ocean biome water color
                    return "#02B0E5";
            }
            else {
                    if (getWaterFog) {
                        if (biomeName.contains("deep"))
                            // Deep Ocean biome water fog color
                            return "#1463a5";

                        // Ocean biome water fog color
                        return "#1165b0";
                    }

                    // Ocean biome water color
                    return "#1787D4";
            }
        }

        // special cases for forest, plains, and field biomes within a cold or temperate climate
        if ((targetBiome.getTemperature() >= 0.3f && targetBiome.getTemperature() < 0.9f)
                && (biomeName.contains("forest") || biomeName.contains("plain") || biomeName.contains("field")))
        {
            if (biomeName.contains("forest")) {
                if (biomeName.contains("dark")) {
                    // Dark Forest biome water color
                    return "#3B6CD1";
                }
                if (varyWaterColor) {
                    if (biomeName.contains("hill")) {
                        // Forest biome water color
                        return "#1E97F2";
                    }

                    // Flower Forest biome water color
                    return "#20A3CC";
                }
                if (biomeName.contains("hill")) {
                    // Wooded Hills biome water color
                    return "#056bd1";
                }

                // Birch Forest Hills biome water color
                return "#0a74c4";
            }

            // Plains biome water color
            return "#44AFF5";
        }

        // for all other cases
        if (targetBiome.getTemperature() < 0.0f) {
                if (biomeName.contains("hill"))
                    // Snowy Taiga Hills water color
                    return "#245b78";

                // Snowy Taiga biome water color
                return "#205e83";
        }
        else if (targetBiome.getTemperature() >= 0.0f && targetBiome.getTemperature() < 0.2f) {
                if (biomeName.contains("river") || biomeName.contains("lake"))
                    // Frozen River biome water color
                    return "#185390";
                if (biomeName.contains("beach") || biomeName.contains("shore"))
                    // Snowy Beach biome water color
                    return "#1463a5";

                // Snowy Tundra biome water color
                return "#14559b";
        }
        else if (targetBiome.getTemperature() >= 0.2f && targetBiome.getTemperature() < 0.25f) {
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
        }
        else if (targetBiome.getTemperature() >= 0.25f && targetBiome.getTemperature() < 0.3f) {
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
        }
        else if (targetBiome.getTemperature() >= 0.3f && targetBiome.getTemperature() < 0.5f) {
                if (biomeName.contains("hill"))
                    // Giant Tree Taiga Hills biome water color
                    return "#286378";

                // Giant Tree Taiga biome water color
                return "#2d6d77";
        }
        else if (targetBiome.getTemperature() >= 0.5f && targetBiome.getTemperature() < 0.6f) {
                // River biome water color
                return "#0084FF";
        }
        else if (targetBiome.getTemperature() >= 0.6f && targetBiome.getTemperature() < 0.7f) {
                if (biomeName.contains("hill"))
                    // Birch Forest Hills biome water color
                    return "#0a74c4";

                // Birch Forest biome water color
                return "#0677ce";
        }
        else if (targetBiome.getTemperature() >= 0.7f && targetBiome.getTemperature() < 0.8f) {
                if (biomeName.contains("dark")) // FOR DARK FOREST BIOMES
                    // Dark Forest biome water color
                    return "#3B6CD1";

                // Forest biome water color
                return "#1E97F2";
        }
        else if (targetBiome.getTemperature() >= 0.8f && targetBiome.getTemperature() < 0.95f) {
                if (biomeName.contains("beach") || biomeName.contains("shore")) {
                    // Beach biome water color
                    return "#157cab";
                }
                if (biomeName.contains("mush")) { // FOR MUSHROOM BIOMES
                    if (biomeName.contains("beach") || biomeName.contains("shore"))
                        // Mushroom Fields Shore biome water color
                        return "#818193";

                    // Mushroom Fields biome water color
                    return "#8a8997";
                }
                if (biomeName.contains("swamp") || biomeName.contains("marsh")) {
                    if (biomeName.contains("hill"))
                        // Swamp Hills biome water color
                        return "#4c6156";

                    // Swamp biome water color
                    return "#4c6559";
                }

                // Plains biome water color
                return "#44AFF5";
        } else if (targetBiome.getTemperature() >= 0.95f && targetBiome.getTemperature() < 1.0f) {
                if (biomeName.contains("jungle") || biomeName.contains("rain")) {
                    if (biomeName.contains("hill"))
                        // Jungle Hills biome water color
                        return "#1B9ED8";

                    // Jungle biome water color
                    return "#14A2C5";
                }

                // Jungle Edge biome water color
                return "#0D8AE3";
        } else if (targetBiome.getTemperature() >= 1.0f && targetBiome.getTemperature() < 2.0f) {
                if (biomeName.contains("savanna")) {
                    if (biomeName.contains("plateau") || varyWaterColor)
                        // Savanna Plateau biome water color
                        return "#2590A8";

                    // Savanna biome water color
                    return "#2C8B9C";
                }
                if (biomeName.contains("hill")) {
                    // Savanna Plateau biome water color
                    return "#2590A8";
                }

                // Savanna biome water color
                return "#2C8B9C";
        } else if (targetBiome.getTemperature() >= 2.0f) {
                if (biomeName.contains("badland")) {
                    if (biomeName.contains("plateau") || varyWaterColor) {
                        if (varyWaterColor)
                            // Eroded Badlands biome water color
                            return "#497F99";

                        // Badlands Plateau biome water color
                        return "#55809E";
                    }

                    // Badlands biome water color
                    return "#4E7F81";
                }
                if (biomeName.contains("hill")) {
                    // Desert Hills biome water color
                    return "#1a7aa1";
                }

                // Desert biome water color
                return "#32A598";
        }
        return Config.getDefaultBiomeConfig().getBiomeProperties().getWaterColor();
    }
}