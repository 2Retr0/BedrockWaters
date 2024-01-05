package retr0.bedrockwaters.config;

import retr0.carrotconfig.config.CarrotConfig;

import java.util.HashMap;

public class BedrockWatersConfig extends CarrotConfig {
    @Entry
    public static boolean enableAlternateFog = true;

    @Entry
    public static boolean enableWaterOpacityBlend = true;

    @Comment public static Comment propertyOverrideNotice;

    @Entry
    public static HashMap<String, Integer> waterColorOverrides = new HashMap<>();

    @Entry
    public static HashMap<String, Integer> waterFogDistanceOverrides = new HashMap<>();

    @Entry
    public static HashMap<String, Float> waterOpacityOverrides = new HashMap<>();
}
