package retr0.bedrockwaters;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.biome.Biome;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static retr0.bedrockwaters.WaterPropertiesReplacer.setBiomeWaterProperties;

public class BedrockWaters implements ModInitializer {
    public static Logger LOGGER         = LogManager.getLogger();

    public static final String MOD_ID   = "bedrockwaters";
    public static final String MOD_NAME = "BedrockWaters";

    @Override
    public void onInitialize() {
        log(Level.INFO, "BedrockWaters initialized!");

        // TODO: REVERT CLIENT ONLY CHANGES!!!j
        // TODO: Lower water sprite opacity
        FabricLoader.getInstance().getModContainer(MOD_ID)
                .map(container -> ResourceManagerHelper.registerBuiltinResourcePack(new Identifier(MOD_ID, "resources"),
                        container, ResourcePackActivationType.NORMAL))
                .filter(success -> !success).ifPresent(success -> LOGGER.warn("Could not register built-in resource pack."));

        log(Level.INFO, "Modifying vanilla biome water attributes...");
        for (Biome currentBiome : BuiltinRegistries.BIOME) {
            String properties = (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) ?
                    " properties -> WaterColor: #" + Integer.toHexString(currentBiome.getWaterColor()) +
                            ", WaterFogColor: #" + Integer.toHexString(currentBiome.getWaterFogColor()) : "";

            WaterPropertiesReplacer.setBiomeWaterProperties(currentBiome);
            log(Level.DEBUG, "Modified " + currentBiome.toString() + properties);
        }
        log(Level.INFO, "Finished!");

        // For modded biomes, we register a callback event which changes their water properties accordingly.
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
}
