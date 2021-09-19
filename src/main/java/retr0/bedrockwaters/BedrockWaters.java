package retr0.bedrockwaters;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BedrockWaters implements ClientModInitializer {
    public static Logger LOGGER         = LogManager.getLogger();

    public static final String MOD_ID   = "bedrockwaters";
    public static final String MOD_NAME = "BedrockWaters";

    @Override
    public void onInitializeClient() {
        log(Level.INFO, "BedrockWaters initialized!");
        // TODO: Lower water sprite opacity

        FabricLoader.getInstance().getModContainer(MOD_ID)
                .map(container -> ResourceManagerHelper.registerBuiltinResourcePack(new Identifier(MOD_ID, "resources"),
                        container, ResourcePackActivationType.DEFAULT_ENABLED))
                .filter(success -> !success).ifPresent(success -> LOGGER.warn("Could not register built-in resource pack."));
    }



    public static void log(Level level, String message) {
        LOGGER.log(level, "["+MOD_NAME+"] " + message);
    }
}
