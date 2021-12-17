package retr0.bedrockwaters.mixin;

import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.world.level.ColorResolver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@SuppressWarnings("unused")
@Mixin(BiomeColors.class)
public interface BiomeColorsAccessor {
    @Mutable @Accessor("WATER_COLOR")
    static void setWaterColor(ColorResolver resolver) {
        throw new AssertionError();
    }
}

