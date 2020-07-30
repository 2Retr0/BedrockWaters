package retr0.bedrockwaters.mixin;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import retr0.bedrockwaters.BiomeExt;

@Mixin(Biome.class)
public abstract class BiomeMixin implements BiomeExt {
    @Shadow @Final protected BiomeEffects effects;

    public void setWaterAttributes(int waterColor, int waterFogColor) {
        this.effects.waterColor = waterColor;
        this.effects.waterFogColor = waterFogColor;
    }
}
