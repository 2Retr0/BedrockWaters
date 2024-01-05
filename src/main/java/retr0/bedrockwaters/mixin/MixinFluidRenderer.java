package retr0.bedrockwaters.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.FluidRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import retr0.bedrockwaters.extension.ExtensionClientWorld;

@Mixin(FluidRenderer.class)
public abstract class MixinFluidRenderer {
    /**
     * Allows water to render with dynamic opacity--incorporating biome blend--based on the biome at {@code pos}.
     */
    @SuppressWarnings("DataFlowIssue")
    @Redirect(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/block/FluidRenderer;" +
                "vertex(Lnet/minecraft/client/render/VertexConsumer;DDDFFFFFI)V"))
    private void redirectVertex(
        FluidRenderer instance,
        // vertex() arguments
        VertexConsumer consumer, double x, double y, double z, float r, float g, float b, float u, float v, int light,
        // render() arguments
        BlockRenderView world, BlockPos pos)
    {
        var a = ((ExtensionClientWorld) MinecraftClient.getInstance().world).bedrockWaters$getOpacity(pos);
        consumer.vertex(x, y, z).color(r, g, b, a).texture(u, v).light(light).normal(0.0f, 1.0f, 0.0f).next();
    }
}
