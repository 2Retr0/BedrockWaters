package retr0.bedrockwaters.mixin;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import retr0.bedrockwaters.IBiome;

@Mixin(Biome.class)
public abstract class BiomeMixin implements IBiome {
    @Shadow @Final private BiomeEffects effects;

    public void setWaterAttributes(int waterColor, int waterFogColor) {
        ((BiomeEffectsMixin) this.effects).setWaterColor(waterColor);
        ((BiomeEffectsMixin) this.effects).setWaterFogColor(waterFogColor);
    }
}
