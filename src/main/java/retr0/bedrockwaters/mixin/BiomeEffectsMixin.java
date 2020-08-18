package retr0.bedrockwaters.mixin;

import net.minecraft.world.biome.BiomeEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BiomeEffects.class)
public interface BiomeEffectsMixin {
    @Accessor("waterColor")
    void setWaterColor(int colorValue);

    @Accessor("waterFogColor")
    void setWaterFogColor(int colorValue);
}
