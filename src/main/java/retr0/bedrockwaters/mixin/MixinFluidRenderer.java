package retr0.bedrockwaters.mixin;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.FluidRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import retr0.bedrockwaters.util.WaterPropertiesUtil;

@Mixin(FluidRenderer.class)
public abstract class MixinFluidRenderer {
    /**
     * Allows water to render with dynamic opacity--incorporating biome blend--based on the positional biome.
     */
    @Redirect(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/block/FluidRenderer;" +
                "vertex(Lnet/minecraft/client/render/VertexConsumer;DDDFFFFFI)V")
    )
    private void redirectVertex(
        FluidRenderer instance,
        // vertex() arguments
        VertexConsumer consumer, double x, double y, double z, float r, float g, float b, float u, float v, int light,
        // render() arguments
        BlockRenderView world, BlockPos pos) {
        // If the mod assets are loaded, render the water with the position's biome's blended water opacity.
        // Since Java Edition water textures determine the opacity of the water, any resource packs that override the
        // mod's water textures should result in the vanilla behavior.
        var a = WaterPropertiesUtil.getBlendedAlpha(pos);
        consumer.vertex(x, y, z).color(r, g, b, a).texture(u, v).light(light).normal(0.0f, 1.0f, 0.0f).next();
    }
}
