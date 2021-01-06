package retr0.bedrockwaters;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.fabric.mixin.biome.VanillaLayeredBiomeSourceAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.biome.Biome;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

import static retr0.bedrockwaters.WaterPropertiesReplacer.setBiomeWaterProperties;

public class BedrockWaters implements ModInitializer {
    public static Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "bedrockwaters";
    public static final String MOD_NAME = "BedrockWaters";

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
        VanillaLayeredBiomeSourceAccessor.getBIOMES().forEach(biomeKey -> {
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
}
