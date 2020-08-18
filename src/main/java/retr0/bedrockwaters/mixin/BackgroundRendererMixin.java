package retr0.bedrockwaters.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BackgroundRenderer.class)
public abstract class BackgroundRendererMixin {
    private static final float DEFAULT_FOG_DISTANCE = getModifiedFogModeEXP2Distance(15);
    private static final float SWAMP_BIOME_FOG_DISTANCE = getModifiedFogModeEXP2Distance(8);
    private static final float RIVER_BIOME_FOG_DISTANCE = getModifiedFogModeEXP2Distance(30);
    private static final float FROZEN_RIVER_BIOME_FOG_DISTANCE = getModifiedFogModeEXP2Distance(20);
    private static final float BEACH_BIOME_FOG_DISTANCE = getModifiedFogModeEXP2Distance(60);
    private static final float SNOWY_BEACH_BIOME_FOG_DISTANCE = getModifiedFogModeEXP2Distance(50);
    private static final float OCEAN_BIOME_FOG_DISTANCE = getModifiedFogModeEXP2Distance(60);

    private static final float TRANSITION_TIME_MS = 5000.0f; // five seconds is also the underwater fog color transition time

    private static boolean transitioning = false;
    private static float waterFogDistance = DEFAULT_FOG_DISTANCE;
    private static ClientPlayerEntity clientPlayerEntity;
    private static Biome biome;
    private static float startingFogDistance;
    private static float startingNextWaterFogDistance;
    private static long startingTime;

    @Inject(method = "applyFog",
            at = @At(
                value = "INVOKE",
                target = "Lcom/mojang/blaze3d/systems/RenderSystem;fogStart(F)V"),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private static void onApplyFogAboveWater(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, CallbackInfo ci, FluidState fluidState, Entity entity)
    {
        clientPlayerEntity = (ClientPlayerEntity) entity;
        biome = clientPlayerEntity.world.getBiome(clientPlayerEntity.getBlockPos());
        waterFogDistance = getWaterFogDistance(biome);
    }

    @Inject(method = "applyFog",
            at = @At(
                value = "INVOKE",
                target = "Lcom/mojang/blaze3d/systems/RenderSystem;fogDensity(F)V",
                shift = At.Shift.AFTER),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private static void onApplyFogUnderWater(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, CallbackInfo ci, FluidState fluidState, Entity entity)
    {
        clientPlayerEntity = (ClientPlayerEntity) entity;
        biome = clientPlayerEntity.world.getBiome(clientPlayerEntity.getBlockPos());
        float nextWaterFogDistance = getWaterFogDistance(biome);

        if (MathHelper.abs(waterFogDistance - nextWaterFogDistance) > 0.0002) {
            // if fog distance is not transitioning or if the fog distance needs to re-transition
            if (!transitioning || startingNextWaterFogDistance != nextWaterFogDistance) {
                // "freeze" the value of variables for time and linear interpolation calculations
                startingFogDistance = waterFogDistance;
                startingNextWaterFogDistance = nextWaterFogDistance;
                startingTime = Util.getMeasuringTimeMs();
                transitioning = true;
            }

            float time = MathHelper.clamp((float) (Util.getMeasuringTimeMs() - startingTime) / TRANSITION_TIME_MS, 0.0f, 1.0f);
            waterFogDistance = MathHelper.lerp(time, startingFogDistance, nextWaterFogDistance);
        } else {
            transitioning = false;
        }

        RenderSystem.fogDensity(waterFogDistance - (clientPlayerEntity.getUnderwaterVisibility() * clientPlayerEntity.getUnderwaterVisibility() * 0.03F));
    }

    private static float getModifiedFogModeEXP2Distance(int distance) {
        // for information on the 'glFogf' OpenGL function: https://docs.microsoft.com/en-us/windows/win32/opengl/glfogf
        return (MathHelper.sqrt(Math.log(distance))/distance) + 0.03f;
    }

    private static float getWaterFogDistance(Biome biome) {
        if (biome.getCategory() == Biome.Category.SWAMP)
            return SWAMP_BIOME_FOG_DISTANCE;

        if (biome.getCategory() == Biome.Category.RIVER)
            // not sure how to determine the exact biome since 1.16.2 changed things
            return (biome.getTemperature() < 0.2f)? FROZEN_RIVER_BIOME_FOG_DISTANCE : RIVER_BIOME_FOG_DISTANCE;

        if (biome.getCategory() == Biome.Category.BEACH)
            // not sure how to determine the exact biome since 1.16.2 changed things
            return (biome.getTemperature() < 0.2f) ? BEACH_BIOME_FOG_DISTANCE : SNOWY_BEACH_BIOME_FOG_DISTANCE;

        if (biome.getCategory() == Biome.Category.OCEAN)
            return OCEAN_BIOME_FOG_DISTANCE;

        return DEFAULT_FOG_DISTANCE;
    }
}