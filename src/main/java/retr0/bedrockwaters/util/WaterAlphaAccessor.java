package retr0.bedrockwaters.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public interface WaterAlphaAccessor {
    default float getAlpha(BlockPos pos) { throw new AssertionError(); }
}
