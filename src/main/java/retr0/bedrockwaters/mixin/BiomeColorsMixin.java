package retr0.bedrockwaters.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.level.ColorResolver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import retr0.bedrockwaters.WaterPropertiesReplacer;

@SuppressWarnings("ConstantConditions")
@Mixin(BiomeColors.class)
public class BiomeColorsMixin {
    @Redirect(method = "getWaterColor",
        at = @At(
            value  = "INVOKE",
            target = "Lnet/minecraft/client/color/world/BiomeColors;getColor(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/level/ColorResolver;)I"))
    private static int getWaterColor(BlockRenderView view, BlockPos pos, ColorResolver resolver) {
        /* While it would be idea to check if BlockRenderView is an instance of ChunkRenderRegion to get the world,
         * some mods (specifically Sodium) use their own implementation of BlockRenderView which I have no idea how
         * to interface with.
         */
        World world = MinecraftClient.getInstance().player.world;

        if (world != null) {
            Biome biome        = world.getBiome(pos);
            Identifier biomeId = world.getRegistryManager().get(Registry.BIOME_KEY).getId(biome);
            return WaterPropertiesReplacer.getBiomeWaterProperties(biome, biomeId, false);
        }
        // Run original method.
        return BiomeColorsInvoker.invokeGetColor(view, pos, BiomeColors.WATER_COLOR);
    }
}
