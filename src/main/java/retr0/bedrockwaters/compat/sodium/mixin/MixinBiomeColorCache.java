package retr0.bedrockwaters.compat.sodium.mixin;

import me.jellysquid.mods.sodium.client.world.biome.BiomeColorCache;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import retr0.bedrockwaters.BedrockWaters;
import retr0.bedrockwaters.util.WaterPropertiesManager;

@Mixin(BiomeColorCache.class)
public abstract class MixinBiomeColorCache {
    @Unique private RegistryEntry<Biome> biomeRegistryEntry;

    @Redirect(
        method = "updateColorBuffers",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/registry/entry/RegistryEntry;value()Ljava/lang/Object;"))
    private <T> T cacheBiomeRegistryEntry(RegistryEntry<T> biome) {
        //noinspection unchecked
        biomeRegistryEntry = (RegistryEntry<Biome>) biome;
        return biome.value();
    }


    @Redirect(
        method = "updateColorBuffers",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/biome/Biome;getWaterColor()I"))
    private int redirectGetWaterColor(Biome instance) {
        return WaterPropertiesManager.getWaterProperties(biomeRegistryEntry).waterColor();
    }
}
