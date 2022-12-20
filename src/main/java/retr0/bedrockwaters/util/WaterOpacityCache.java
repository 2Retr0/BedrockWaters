package retr0.bedrockwaters.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.BiomeColorCache;
import net.minecraft.util.CuboidBlockIterator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Unique;

import static retr0.bedrockwaters.util.WaterPropertiesManager.getWaterProperties;

public class WaterOpacityCache {
    private final BiomeColorCache opacityCache = new BiomeColorCache(pos -> (int) (getAverageOpacity(pos) * 100f));

    private final MinecraftClient client;
    private final World world;

    public WaterOpacityCache(MinecraftClient client, World world) {
        this.client = client;
        this.world = world;
    }



    /**
     * @return The cached biome blend setting-dependent water opacity at {@code pos}; or, {@code 1.0f} if the bedrock
     * water textures in the BedrockWaters default resource pack are not loaded (e.g., overwritten by another resource
     * pack).
     */
    public float getBiomeOpacity(BlockPos pos) {
        return ResourceManager.areModResourcesLoaded() ? (float) opacityCache.getBiomeColor(pos) / 100f : 1.0f;
    }



    public void reset() { opacityCache.reset(); }

    public void reset(int chunkX, int chunkZ) { opacityCache.reset(chunkX, chunkZ); }



    /**
     * @return The average opacity of all water blocks within the current biome blend radius.
     */
    @SuppressWarnings("ConstantConditions")
    @Unique
    private float getAverageOpacity(BlockPos pos) {
        var radius = client.options.getBiomeBlendRadius().getValue();

        if (radius == 0) {
            return getWaterProperties(world.getBiome(pos)).waterOpacity();
        } else {
            var it = new CuboidBlockIterator(
                pos.getX() - radius, pos.getY(), pos.getZ() - radius,
                pos.getX() + radius, pos.getY(), pos.getZ() + radius);

            float totalOpacity = 0f;
            float opacity;
            for (var mutable = new BlockPos.Mutable(); it.step(); totalOpacity += opacity) {
                mutable.set(it.getX(), it.getY(), it.getZ());
                opacity = getWaterProperties(world.getBiome(mutable)).waterOpacity();
            }

            return totalOpacity / MathHelper.square((radius * 2 + 1));
        }
    }

}
