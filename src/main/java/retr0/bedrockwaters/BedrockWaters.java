package retr0.bedrockwaters;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retr0.bedrockwaters.config.BedrockWatersConfig;
import retr0.bedrockwaters.util.ResourceManager;
import retr0.bedrockwaters.util.WaterPropertiesManager;
import retr0.carrotconfig.config.CarrotConfig;

public class BedrockWaters implements ClientModInitializer {
    public static final String MOD_ID = "bedrockwaters";
    public static Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("{} initialized!", MOD_ID);
        CarrotConfig.init(MOD_ID, BedrockWatersConfig.class);

        ResourceManager.init();
        WaterPropertiesManager.init();
    }
}
