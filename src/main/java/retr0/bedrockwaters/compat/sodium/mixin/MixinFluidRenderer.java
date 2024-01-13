package retr0.bedrockwaters.compat.sodium.mixin;

import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.FluidRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo @Mixin(FluidRenderer.class)
public abstract class MixinFluidRenderer {

}
