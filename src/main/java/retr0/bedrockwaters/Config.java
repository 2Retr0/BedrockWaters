package retr0.bedrockwaters;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import org.apache.logging.log4j.Level;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Config {
    private static Config config;

    private final boolean childBiomesInheritParentColors = true;
    private final boolean autoConfigureVanillaBiomesOnly = false;
    private final List<BiomeConfig> biomes = new ArrayList<>();

    private Config() {
        // add a default attribute element
        biomes.add(BiomeConfig.builder().biomeId("DEFAULT").biomeProperties(BiomeProperties.builder().waterColor("#44AFF5").build()).build());

        // add default config biomes sorted by rawID
        biomes.add(BiomeConfig.builder().biomeId("minecraft:ocean").biomeProperties(BiomeProperties.builder().waterColor("#1787D4").waterFogColor("#1165b0").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:plains").biomeProperties(BiomeProperties.builder().waterColor("#44AFF5").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:desert").biomeProperties(BiomeProperties.builder().waterColor("#32A598").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:mountains").biomeProperties(BiomeProperties.builder().waterColor("#007BF7").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:forest").biomeProperties(BiomeProperties.builder().waterColor("#1E97F2").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:taiga").biomeProperties(BiomeProperties.builder().waterColor("#287082").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:swamp").biomeProperties(BiomeProperties.builder().waterColor("#4c6559").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:river").biomeProperties(BiomeProperties.builder().waterColor("#0084FF").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:nether_wastes").biomeProperties(BiomeProperties.builder().waterColor("#905957").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:the_end").biomeProperties(BiomeProperties.builder().waterColor("#62529e").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:frozen_ocean").biomeProperties(BiomeProperties.builder().waterColor("#2570B5").waterFogColor("#174985").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:frozen_river").biomeProperties(BiomeProperties.builder().waterColor("#185390").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:snowy_tundra").biomeProperties(BiomeProperties.builder().waterColor("#14559b").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:snowy_mountains").biomeProperties(BiomeProperties.builder().waterColor("#1156a7").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:mushroom_fields").biomeProperties(BiomeProperties.builder().waterColor("#8a8997").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:mushroom_field_shore").biomeProperties(BiomeProperties.builder().waterColor("#818193").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:beach").biomeProperties(BiomeProperties.builder().waterColor("#157cab").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:desert_hills").biomeProperties(BiomeProperties.builder().waterColor("#1a7aa1").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:wooded_hills").biomeProperties(BiomeProperties.builder().waterColor("#056bd1").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:taiga_hills").biomeProperties(BiomeProperties.builder().waterColor("#236583").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:mountain_edge").biomeProperties(BiomeProperties.builder().waterColor("#045cd5").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:jungle").biomeProperties(BiomeProperties.builder().waterColor("#14A2C5").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:jungle_hills").biomeProperties(BiomeProperties.builder().waterColor("#1B9ED8").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:jungle_edge").biomeProperties(BiomeProperties.builder().waterColor("#0D8AE3").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:deep_ocean").biomeProperties(BiomeProperties.builder().waterColor("#1787D4").waterFogColor("#1463a5").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:stone_shore").biomeProperties(BiomeProperties.builder().waterColor("#0d67bb").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:snowy_beach").biomeProperties(BiomeProperties.builder().waterColor("#1463a5").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:birch_forest").biomeProperties(BiomeProperties.builder().waterColor("#0677ce").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:birch_forest_hills").biomeProperties(BiomeProperties.builder().waterColor("#0a74c4").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:dark_forest").biomeProperties(BiomeProperties.builder().waterColor("#3B6CD1").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:snowy_taiga").biomeProperties(BiomeProperties.builder().waterColor("#205e83").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:snowy_taiga_hills").biomeProperties(BiomeProperties.builder().waterColor("#245b78").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:giant_tree_taiga").biomeProperties(BiomeProperties.builder().waterColor("#2d6d77").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:giant_tree_taiga_hills").biomeProperties(BiomeProperties.builder().waterColor("#2d6d77").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:wooded_mountains").biomeProperties(BiomeProperties.builder().waterColor("#0E63AB").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:savanna").biomeProperties(BiomeProperties.builder().waterColor("#2C8B9C").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:savanna_plateau").biomeProperties(BiomeProperties.builder().waterColor("#2590A8").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:badlands").biomeProperties(BiomeProperties.builder().waterColor("#4E7F81").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:wooded_badlands_plateau").biomeProperties(BiomeProperties.builder().waterColor("#497F99").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:badlands_plateau").biomeProperties(BiomeProperties.builder().waterColor("#55809E").build()).build());

        /* THE END BIOMES */
        biomes.add(BiomeConfig.builder().biomeId("minecraft:small_end_islands").biomeProperties(BiomeProperties.builder().waterColor("#62529e").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:end_midlands").biomeProperties(BiomeProperties.builder().waterColor("#62529e").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:end_highlands").biomeProperties(BiomeProperties.builder().waterColor("#62529e").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:end_barrens").biomeProperties(BiomeProperties.builder().waterColor("#62529e").build()).build());

        /* NEW OCEAN BIOMES */
        biomes.add(BiomeConfig.builder().biomeId("minecraft:warm_ocean").biomeProperties(BiomeProperties.builder().waterColor("#02B0E5").waterFogColor("#0289d5").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:lukewarm_ocean").biomeProperties(BiomeProperties.builder().waterColor("#0D96DB").waterFogColor("#0a74c4").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:cold_ocean").biomeProperties(BiomeProperties.builder().waterColor("#2080C9").waterFogColor("#14559b").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:deep_warm_ocean").biomeProperties(BiomeProperties.builder().waterColor("#02B0E5").waterFogColor("#0686ca").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:deep_lukewarm_ocean").biomeProperties(BiomeProperties.builder().waterColor("#0D96DB").waterFogColor("#0e72b9").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:deep_cold_ocean").biomeProperties(BiomeProperties.builder().waterColor("#2080C9").waterFogColor("#185390").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:deep_frozen_ocean").biomeProperties(BiomeProperties.builder().waterColor("#2570B5").waterFogColor("#1a4879").build()).build());

        biomes.add(BiomeConfig.builder().biomeId("minecraft:sunflower_plains").biomeProperties(BiomeProperties.builder().waterColor("#44AFF5").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:gravelly_mountains").biomeProperties(BiomeProperties.builder().waterColor("#0E63AB").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:flower_forest").biomeProperties(BiomeProperties.builder().waterColor("#20A3CC").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:taiga_mountains").biomeProperties(BiomeProperties.builder().waterColor("#1E6B82").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:swamp_hills").biomeProperties(BiomeProperties.builder().waterColor("#4c6156").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:ice_spikes").biomeProperties(BiomeProperties.builder().waterColor("#14559b").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:modified_jungle").biomeProperties(BiomeProperties.builder().waterColor("#1B9ED8").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:modified_jungle_edge").biomeProperties(BiomeProperties.builder().waterColor("#0D8AE3").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:snowy_taiga_mountains").biomeProperties(BiomeProperties.builder().waterColor("#205e83").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:giant_spruce_taiga").biomeProperties(BiomeProperties.builder().waterColor("#2d6d77").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:giant_spruce_taiga_hills").biomeProperties(BiomeProperties.builder().waterColor("#286378").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:modified_gravelly_mountains").biomeProperties(BiomeProperties.builder().waterColor("#0E63AB").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:shattered_savanna").biomeProperties(BiomeProperties.builder().waterColor("#2590A8").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:eroded_badlands").biomeProperties(BiomeProperties.builder().waterColor("#497F99").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:modified_badlands_plateau").biomeProperties(BiomeProperties.builder().waterColor("#55809E").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:bamboo_jungle").biomeProperties(BiomeProperties.builder().waterColor("#14A2C5").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:bamboo_jungle_hills").biomeProperties(BiomeProperties.builder().waterColor("#1B9ED8").build()).build());

        /* NETHER BIOMES */
        // these color values are unofficial!
        biomes.add(BiomeConfig.builder().biomeId("minecraft:soul_sand_valley").biomeProperties(BiomeProperties.builder().waterColor("#968989").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:crimson_forest").biomeProperties(BiomeProperties.builder().waterColor("#91211b").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:warped_forest").biomeProperties(BiomeProperties.builder().waterColor("#512450").build()).build());
        biomes.add(BiomeConfig.builder().biomeId("minecraft:basalt_deltas").biomeProperties(BiomeProperties.builder().waterColor("#474656").build()).build());
    }

    public static void init(File configDir) throws IOException {
        int biomesSize;
        config = new Config();

        // check for config directory
        if (!configDir.exists()) configDir.mkdirs();
        File configFile = new File(configDir, "config.json");

        if (configFile.exists()) {
            try (Reader reader = new FileReader(configFile)) {
                config = new Gson().fromJson(reader, Config.class);
            }
            biomesSize = config.biomes.size();

            // remove any incorrectly formatted entries
            config.biomes.removeIf(BiomeConfig -> BiomeConfig.biomeId == null || BiomeConfig.biomeProperties == null);
            if (config.biomes.size() < biomesSize)
                BedrockWaters.log(Level.WARN, "There are incorrectly formatted entries in the configuration file! Such entries have been skipped.");
        } else {
            try (JsonWriter writer = new JsonWriter(new FileWriter(configFile))) {
                writer.setIndent("\t");
                new Gson().toJson(config, Config.class, writer);
            }
        }
    }

    public static boolean canChildBiomeInheritParentColors() { return config.childBiomesInheritParentColors; }

    public static boolean allowVanillaBiomesOnly() { return config.autoConfigureVanillaBiomesOnly; }

    public static BiomeConfig getBiomeFromBiomeConfig(Biome targetBiome) {
        // check for targetBiome in config
        for (BiomeConfig biomeConfig : config.biomes) {
            if (targetBiome.equals(Registry.BIOME.get(Identifier.tryParse(biomeConfig.biomeId))))
                return biomeConfig;
        }
        return null;
    }

    public static BiomeConfig getDefaultBiomeConfig() {
        for (BiomeConfig biomeConfig : config.biomes) {
            if (biomeConfig.biomeId.toUpperCase().equals("DEFAULT"))
                return biomeConfig;
        }
        return null;
    }

    @Builder
    public static class BiomeConfig {
        private final String biomeId;
        @Getter private final BiomeProperties biomeProperties;
    }

    @Builder
    public static class BiomeProperties {
        @Getter private final String waterColor;
        @Getter private final String waterFogColor;

        public static class BiomePropertiesBuilder {
            public BiomePropertiesBuilder waterColor(String waterColor) {
                this.waterColor = waterColor;

                // if only waterColor is initialized set waterFogColor to waterColor
                if (this.waterFogColor == null)
                    this.waterFogColor = waterColor;

                return this;
            }
        }
    }
}
