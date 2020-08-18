package retr0.bedrockwaters;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
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

        // TODO: Lower water sprite opacity
        final Identifier bedrockWaterStillSpriteId = new Identifier(MOD_ID, "block/water_still");
        final Identifier bedrockWaterFlowSpriteId = new Identifier(MOD_ID, "block/water_flow");

        // add Bedrock Edition water sprites to the block atlas
        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEX).register((atlasTexture, registry) -> {
            registry.register(bedrockWaterStillSpriteId);
            registry.register(bedrockWaterFlowSpriteId);
        });

        try {
            Config.init(new File("./config/"+MOD_ID+"/"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        log(Level.INFO, "Modifying vanilla biome water attributes...");
        // loop through all registered vanilla biomes
        BuiltinRegistries.BIOME.forEach(Biome -> {
            WaterPropertiesReplacer.setBiomeWaterProperties(Biome);
            log(Level.DEBUG, "Modified " + BuiltinRegistries.BIOME.getId(Biome) + " properties -> "
                    + "WaterColor: #" + Integer.toHexString(Biome.getWaterColor()) + ", "
                    + "WaterFogColor: #" + Integer.toHexString(Biome.getWaterFogColor()));
        });
        log(Level.INFO, "Finished!");

        // for modded biomes, we register a callback event which changes their water properties accordingly
        RegistryEntryAddedCallback.event(BuiltinRegistries.BIOME).register((i, identifier, biome) -> {
            setBiomeWaterProperties(biome);
            log(Level.DEBUG, "Modified " + identifier + " properties -> "
                    + "WaterColor: #" + Integer.toHexString(biome.getWaterColor()) + ", "
                    + "WaterFogColor: #" + Integer.toHexString(biome.getWaterFogColor()));
        });
    }

    public static void log(Level level, String message){
        LOGGER.log(level, "["+MOD_NAME+"] " + message);
    }
}