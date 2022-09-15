package retr0.bedrockwaters.compat.sodium.mixin;

import me.jellysquid.mods.sodium.client.model.quad.ModelQuadView;
import me.jellysquid.mods.sodium.client.render.pipeline.FluidRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import retr0.bedrockwaters.util.WaterPropertiesUtil;

import static me.jellysquid.mods.sodium.client.util.color.ColorABGR.*;

@Mixin(FluidRenderer.class)
public abstract class MixinFluidRenderer {
    /**
     * Functions identically to {@link me.jellysquid.mods.sodium.client.util.color.ColorABGR#mul(int, float)} but
     * alters the alpha to reflect dynamic water opacity--incorporating biome blend--based on the biome at {@code pos}.
     */
    @Redirect(
        method = "calculateQuadColors",
        at = @At(
            value = "INVOKE",
            target = "Lme/jellysquid/mods/sodium/client/util/color/ColorABGR;mul(IF)I"),
        remap = false
    )
    private int redirectMul(
        // calculateQuadColors() arguments
        int color, float w,
        // mul() arguments
        ModelQuadView quad, BlockRenderView world, BlockPos pos)
    {
        float r = w * unpackRed(color);
        float g = w * unpackGreen(color);
        float b = w * unpackBlue(color);
        float a = 255 * WaterPropertiesUtil.getBlendedAlpha(pos);

        return pack((int) r, (int) g, (int) b, (int) a);
    }
}
