package retr0.bedrockwaters;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

public class WaterPropertiesReplacer {
    public static void setBiomeWaterProperties(Biome targetBiome) {
        int waterColor;
        int waterFogColor;

        Identifier targetBiomeId = Registry.BIOME.getId(targetBiome);
        Config.BiomeConfig biomeConfig = Config.getBiomeFromBiomeConfig(targetBiome);
        Config.BiomeConfig parentConfig = null;
        Config.BiomeConfig defaultConfig = Config.getDefaultBiomeConfig();

        // only set parentConfig if targetBiome has a parent as Identifier.tryParse() will throw a NullPointerException
        if (targetBiome.hasParent())
            parentConfig = Config.getBiomeFromBiomeConfig(Registry.BIOME.get(Identifier.tryParse(targetBiome.getParent())));

        // if biome has a corresponding BiomeConfig
        if (biomeConfig != null)
        {
            waterColor = hexStringToInt(biomeConfig.getBiomeProperties().getWaterColor());
            waterFogColor = hexStringToInt(biomeConfig.getBiomeProperties().getWaterFogColor());

            ((BiomeExt)targetBiome).setWaterAttributes(waterColor, waterFogColor);
        }
        // otherwise if the biome has a parent, use parent biome properties
        else if ((targetBiomeId.getNamespace().equals("minecraft") || !Config.allowVanillaBiomesOnly())
                && Config.canChildBiomeInheritParentColors()
                && parentConfig != null)
        {
            waterColor = hexStringToInt(parentConfig.getBiomeProperties().getWaterColor());
            waterFogColor = hexStringToInt(parentConfig.getBiomeProperties().getWaterFogColor());

            ((BiomeExt)targetBiome).setWaterAttributes(waterColor, waterFogColor);
        }
        // otherwise use default biome properties
        // (if the biome is modded, only change water properties if the water color is the vanilla water color)
        else if (defaultConfig != null
                && ((!targetBiomeId.getNamespace().equals("minecraft") && targetBiome.getWaterColor() == 4159204)
                    || (targetBiomeId.getNamespace().equals("minecraft"))))
        {
            waterColor = hexStringToInt(defaultConfig.getBiomeProperties().getWaterColor());
            waterFogColor = hexStringToInt(defaultConfig.getBiomeProperties().getWaterFogColor());

            ((BiomeExt)targetBiome).setWaterAttributes(waterColor, waterFogColor);
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
}
