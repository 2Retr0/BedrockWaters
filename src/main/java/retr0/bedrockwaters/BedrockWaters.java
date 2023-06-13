package retr0.bedrockwaters;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retr0.bedrockwaters.util.ResourceManager;

public class BedrockWaters implements ClientModInitializer {
    public static final String MOD_ID = "bedrockwaters";
    public static final String MOD_NAME = "BedrockWaters";

    public static Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info(MOD_NAME + " initialized!");

        ResourceManager.register();
//        CarrotConfig.init(MOD_ID, BedrockWatersConfig.class);
    }
}
