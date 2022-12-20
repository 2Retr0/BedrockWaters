package retr0.bedrockwaters.extension;

import net.minecraft.util.math.BlockPos;

public interface ExtensionClientWorld {
    /**
     * @param pos The {@link BlockPos} at the target water block.
     * @return The biome blend setting-dependent water opacity at {@code pos}.
     */
    default float getOpacity(BlockPos pos) { throw new AssertionError(); }
}
