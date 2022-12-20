package retr0.bedrockwaters.compat.iris.mixin;

import me.jellysquid.mods.sodium.client.model.light.LightPipeline;
import me.jellysquid.mods.sodium.client.model.quad.ModelQuadView;
import me.jellysquid.mods.sodium.client.model.quad.blender.ColorSampler;
import me.jellysquid.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuilder;
import me.jellysquid.mods.sodium.client.render.pipeline.FluidRenderer;
import net.coderbot.iris.block_rendering.BlockRenderingSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import retr0.bedrockwaters.extension.ExtensionClientWorld;

@Mixin(FluidRenderer.class)
public abstract class MixinFluidRenderer {
    @Shadow(remap=false) @Final @Mutable private int[] quadColors;

    @Unique private boolean useSeparateAo;



    /**
     * Caches the {@code shouldUseSeparateAo} value (behavior identical in Iris's {@code MixinFluidRenderer}).
     */
    @Inject(method = "render", at = @At("HEAD"))
    private void cacheAo(
        BlockRenderView world, FluidState fluidState, BlockPos pos, BlockPos offset, ChunkModelBuilder buffers,
        CallbackInfoReturnable<Boolean> cir)
    {
        useSeparateAo = BlockRenderingSettings.INSTANCE.shouldUseSeparateAo();
    }



    /**
     * Alters quad colors to reflect dynamic water opacity--incorporating biome blend--based on the biome at {@code pos}
     * after all other calculations have been done.
     */
    // Iris's MixinFluidRenderer redirects ColorABGR#mul() so we must instead patch alpha after.
    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "calculateQuadColors", at = @At("TAIL"))
    private void injectAlpha(
        ModelQuadView quad, BlockRenderView world, BlockPos pos, LightPipeline lighter, Direction dir, float brightness,
        ColorSampler<FluidState> colorSampler, FluidState fluidState, CallbackInfo ci)
    {
        // Don't patch alpha if some separate AO is set to be used (keeping shaders at their default appearance!).
        if (useSeparateAo) return;

        var alpha = ((ExtensionClientWorld) MinecraftClient.getInstance().world).getOpacity(pos);
        for (int i = 0; i < 4; ++i) {
            quadColors[i] = multiplyAlpha(quadColors[i], alpha);
        }
    }



    /**
     * @param color An ABGR formatted color.
     * @param factor The factor to which the alpha will be multiplied.
     * @return The alpha-multiplied color.
     */
    private static int multiplyAlpha(int color, float factor) {
        int alpha = (int) (factor * (color >> 24 & 0xFF));

        return (alpha & 0xFF) << 24 | (color & 0xFFFFFF);
    }
}
