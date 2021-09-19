package retr0.bedrockwaters.mixin;

import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.level.ColorResolver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BiomeColors.class)
public interface BiomeColorsInvoker {
    @Invoker
    static int invokeGetColor(BlockRenderView view, BlockPos pos, ColorResolver resolver) {
        throw new AssertionError();
    }
}
