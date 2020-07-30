package retr0.bedrockwaters;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class BedrockWaters implements ModInitializer {

    public static Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "bedrockwaters";
    public static final String MOD_NAME = "BedrockWaters";

    @Override
    public void onInitialize() {
        log(Level.INFO, "Initializing");
        int waterColor;
        int waterFogColor;

        try {
            Config.init(new File("./config/"+MOD_ID+"/"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // loop through all registered biomes
        Set<Identifier> biomes = Registry.BIOME.getIds();
        for(Identifier biomeId : biomes) {
            Config.BiomeConfig biomeConfig = Config.getBiomeFromBiomeConfig(biomeId);
            Config.BiomeConfig defaultConfig = Config.getDefaultBiomeConfig();

            // if biome has a corresponding BiomeConfig
            if(biomeConfig != null) {
                waterColor = hexToInt(biomeConfig.getBiomeProperties().getWaterColor());
                waterFogColor = hexToInt(biomeConfig.getBiomeProperties().getWaterFogColor());

                ((BiomeExt) Registry.BIOME.get(biomeId)).setWaterAttributes(waterColor, waterFogColor);
                log(Level.DEBUG,"Changed " + biomeId + "properties -> WaterColor: #" + Integer.toHexString(waterColor) + ", WaterFogColor: #" + Integer.toHexString(waterFogColor));
            }
            // otherwise if biome has a parent, use parent biome properties
            else if ((biomeId.getNamespace().equals("minecraft") || !Config.allowVanillaBiomesOnly())
                    && Config.canChildBiomeInheritParentColors()
                    && Registry.BIOME.get(biomeId).hasParent()) {
                waterColor = Registry.BIOME.get(Identifier.tryParse(Registry.BIOME.get(biomeId).getParent())).getWaterColor();
                waterFogColor = Registry.BIOME.get(Identifier.tryParse(Registry.BIOME.get(biomeId).getParent())).getWaterFogColor();

                ((BiomeExt) Registry.BIOME.get(biomeId)).setWaterAttributes(waterColor, waterFogColor);
                log(Level.DEBUG,"Changed " + biomeId + "properties -> WaterColor: #" + Integer.toHexString(waterColor) + ", WaterFogColor: #" + Integer.toHexString(waterFogColor));
            }
            // otherwise use default biome properties
            else if (biomeId.getNamespace().equals("minecraft") || !Config.allowVanillaBiomesOnly()) {
                waterColor = hexToInt(defaultConfig.getBiomeProperties().getWaterColor());
                waterFogColor = hexToInt(defaultConfig.getBiomeProperties().getWaterFogColor());

                ((BiomeExt) Registry.BIOME.get(biomeId)).setWaterAttributes(waterColor, waterFogColor);
                log(Level.DEBUG, "Changed " + biomeId + "properties -> WaterColor: #" + Integer.toHexString(waterColor) + ", WaterFogColor: #" + Integer.toHexString(waterFogColor));
            }
        }
    }

    private static int hexToInt(String hexString) {
        if(hexString.charAt(0) == '#')
            hexString = hexString.replace("#", "");
        return Integer.parseInt(hexString, 16);
    }

    public static void log(Level level, String message){
        LOGGER.log(level, "["+MOD_NAME+"] " + message);
    }
}