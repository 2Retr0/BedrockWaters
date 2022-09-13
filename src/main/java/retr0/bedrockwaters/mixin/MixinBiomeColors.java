package retr0.bedrockwaters.mixin;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.level.ColorResolver;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import retr0.bedrockwaters.util.WaterPropertiesUtil;

import static retr0.bedrockwaters.BedrockWaters.LOGGER;

@Mixin(BiomeColors.class)
public abstract class MixinBiomeColors {
    @Shadow @Final @Mutable public static ColorResolver WATER_COLOR;

    private static Registry<Biome> biomeRegistry;

    static {
        // Register a new listener for when the client play network handler is ready to send packets to the server.
        // Whenever the client loads a world, we update the biome registry to reference that of the loaded world's
        // dynamic registry.
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            biomeRegistry = handler.getWorld().getRegistryManager().get(Registry.BIOME_KEY);
        });


        // We set BiomeColors#WATER_COLOR to a new handler which will return our patched biomes' water colors. As the
        // biome's registry key and entry is dependent on world's dynamic registry, we will use our maintained registry.
        WATER_COLOR = (biome, x, z) -> {
            // If the client is on a server, we patch the biome water colors dynamically when requested.
            var biomeKey = biomeRegistry.getKey(biome).orElse(null);
            var biomeRef = biomeRegistry.getEntry(biomeKey).orElse(null);

            if (biomeKey == null || biomeRef == null) {
                LOGGER.error(biome + " could not be found in the client world's registry and was not patched!");
                return biome.getWaterColor();
            }

            return WaterPropertiesUtil.getWaterProperties(biomeKey, biomeRef).waterColor();
        };
    }
}
