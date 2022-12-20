package retr0.bedrockwaters.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import retr0.bedrockwaters.extension.ExtensionClientWorld;
import retr0.bedrockwaters.util.WaterOpacityCache;

@Mixin(ClientWorld.class)
public abstract class MixinClientWorld implements ExtensionClientWorld {
    @Unique public final WaterOpacityCache waterOpacityCache =
        new WaterOpacityCache(MinecraftClient.getInstance(), (ClientWorld) (Object) this);


    @Unique @Override
    public float getOpacity(BlockPos pos) {
        return waterOpacityCache.getBiomeOpacity(pos);
    }



    @Inject(method = "resetChunkColor", at = @At("HEAD"))
    private void resetOpacityCacheChunk(ChunkPos chunkPos, CallbackInfo ci) {
        waterOpacityCache.reset(chunkPos.x, chunkPos.z);
    }



    @Inject(method = "reloadColor", at = @At("HEAD"))
    private void resetOpacityCache(CallbackInfo ci) {
        waterOpacityCache.reset();
    }
}
