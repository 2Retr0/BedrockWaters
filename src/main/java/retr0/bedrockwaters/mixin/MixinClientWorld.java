package retr0.bedrockwaters.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.BiomeColorCache;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.CuboidBlockIterator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import retr0.bedrockwaters.util.WaterPropertiesUtil.WaterAlphaAccessor;

import static retr0.bedrockwaters.util.WaterPropertiesUtil.getWaterProperties;

@Mixin(ClientWorld.class)
public abstract class MixinClientWorld implements WaterAlphaAccessor {
    @Unique
    private final BiomeColorCache alphaCache = new BiomeColorCache(pos -> (int) (calculateAlpha(pos) * 100f));

    /**
     * @param pos The {@link BlockPos} at the target water block.
     * @return The average opacity of all water blocks within the current biome blend radius.
     */
    @SuppressWarnings("ConstantConditions")
    @Unique
    private float calculateAlpha(BlockPos pos) {
        var client = MinecraftClient.getInstance();
        var radius = client.options.getBiomeBlendRadius().getValue();

        if (radius == 0) {
            return getWaterProperties(client.world.getBiome(pos)).waterAlpha();
        } else {
            var it = new CuboidBlockIterator(
                pos.getX() - radius, pos.getY(), pos.getZ() - radius,
                pos.getX() + radius, pos.getY(), pos.getZ() + radius);

            float totalAlpha = 0f;
            float alpha;
            for (var mutable = new BlockPos.Mutable(); it.step(); totalAlpha += alpha) {
                mutable.set(it.getX(), it.getY(), it.getZ());
                alpha = getWaterProperties(client.world.getBiome(mutable)).waterAlpha();
            }

            return totalAlpha / MathHelper.square((radius * 2 + 1));
        }
    }



    /**
     * An implementation of {@link WaterAlphaAccessor#getAlpha(BlockPos)} backed by {@link BiomeColorCache}.
     */
    @Unique @Override
    public float getAlpha(BlockPos pos) {
        return (float) alphaCache.getBiomeColor(pos) / 100f;
    }



    /**
     * Makes the method also reset {@link MixinClientWorld#alphaCache}.
     */
    @Inject(method = "resetChunkColor", at = @At("HEAD"))
    private void onResetChunkColor(ChunkPos chunkPos, CallbackInfo ci) {
        alphaCache.reset(chunkPos.x, chunkPos.z);
    }



    /**
     * Makes the method also reset {@link MixinClientWorld#alphaCache}.
     */
    @Inject(method = "reloadColor", at = @At("HEAD"))
    private void onReloadColor(CallbackInfo ci) {
        alphaCache.reset();
    }
}
