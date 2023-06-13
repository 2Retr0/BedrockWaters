package retr0.bedrockwaters.compat.sodium.mixin;

import me.jellysquid.mods.sodium.client.model.quad.ModelQuadView;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.FluidRenderer;
import me.jellysquid.mods.sodium.client.util.color.ColorABGR;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import retr0.bedrockwaters.extension.ExtensionClientWorld;

@Pseudo @Mixin(FluidRenderer.class)
public abstract class MixinFluidRenderer {
    /**
     * Functions identically to {@link me.jellysquid.mods.sodium.client.util.color.ColorABGR#mul(int, float)} but
     * alters the alpha to reflect dynamic water opacity--incorporating biome blend--based on the biome at {@code pos}.
     */
    @SuppressWarnings("DataFlowIssue")
    @Redirect(
        method = "updateQuad",
        at = @At(
            value = "INVOKE",
            target = "Lme/jellysquid/mods/sodium/client/util/color/ColorABGR;mul(IF)I"),
        remap = false)
    private int redirectMul(
        // calculateQuadColors() arguments
        int color, float alpha,
        // mul() arguments
        ModelQuadView quad, BlockRenderView world, BlockPos pos)
    {
        float r = ColorABGR.unpackRed(color);
        float g = ColorABGR.unpackGreen(color);
        float b = ColorABGR.unpackBlue(color);
        float a = 255 * ((ExtensionClientWorld) MinecraftClient.getInstance().world).getOpacity(pos) * alpha;

        return ColorABGR.pack((int) r, (int) g, (int) b, (int) a);
    }

//    @Inject(
//        method = "writeQuad",
//        at = @At("TAIL"),
//        locals = LocalCapture.CAPTURE_FAILSOFT)
//    private void modifyColor(
//        ChunkModelBuilder builder, ChunkRenderBounds.Builder bounds, Material material, BlockPos offset, ModelQuadView quad,
//        ModelQuadFacing facing, boolean flip, CallbackInfo ci, ChunkVertexEncoder.Vertex[] vertices, Sprite sprite,
//        ChunkMeshBufferBuilder vertexBuffer)
//    {
//        ;
//    }
}
