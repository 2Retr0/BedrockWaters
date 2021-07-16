package retr0.bedrockwaters.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
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
    // TODO: MOVE THESE TO BIOME PROPERTIES?
    private static final float DEFAULT_FOG_DISTANCE            = 15;
    private static final float SWAMP_BIOME_FOG_DISTANCE        = 8;
    private static final float RIVER_BIOME_FOG_DISTANCE        = 30;
    private static final float FROZEN_RIVER_BIOME_FOG_DISTANCE = 20;
    private static final float BEACH_BIOME_FOG_DISTANCE        = 60;
    private static final float SNOWY_BEACH_BIOME_FOG_DISTANCE  = 50;
    private static final float OCEAN_BIOME_FOG_DISTANCE        = 60;

    // Five seconds seems like a reasonable underwater fog color transition time.
    private static final float TRANSITION_TIME_MS = 5000.0f;

    private static boolean transitioning = false;
    private static float waterFogDistance = DEFAULT_FOG_DISTANCE;
    private static ClientPlayerEntity clientPlayerEntity;
    private static Biome biome;
    private static float startingFogDistance;
    private static float startingNextWaterFogDistance;
    private static long startingTime;

    @Inject(method = "applyFog(Lnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/BackgroundRenderer$FogType;FZ)V",
            at = @At(
                value   = "INVOKE",
                target  = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderFogStart(F)V",
                ordinal = 1),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private static void onApplyFogAboveWater(Camera camera,
                                             BackgroundRenderer.FogType fogType,
                                             float viewDistance,
                                             boolean thickFog,
                                             CallbackInfo ci)
    {
        clientPlayerEntity = (ClientPlayerEntity) camera.getFocusedEntity();
        biome              = clientPlayerEntity.world.getBiome(clientPlayerEntity.getBlockPos());
        waterFogDistance   = getWaterFogDistance(biome);
    }



    @Inject(method = "applyFog(Lnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/BackgroundRenderer$FogType;FZ)V",
            at = @At(
                value   = "INVOKE",
                target  = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderFogEnd(F)V",
                ordinal = 0,
                shift   = At.Shift.AFTER),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void onApplyFogUnderWater(Camera camera,
                                             BackgroundRenderer.FogType fogType,
                                             float viewDistance,
                                             boolean thickFog,
                                             CallbackInfo ci)
    {
        clientPlayerEntity         = (ClientPlayerEntity) camera.getFocusedEntity();
        biome                      = clientPlayerEntity.world.getBiome(clientPlayerEntity.getBlockPos());
        float nextWaterFogDistance = getWaterFogDistance(biome);

        if (MathHelper.abs(waterFogDistance - nextWaterFogDistance) > 0.0002) {
            // If fog distance is not transitioning or if the fog distance needs to re-transition.
            if (!transitioning || startingNextWaterFogDistance != nextWaterFogDistance) {
                // "freeze" the value of variables for time and linear interpolation calculations.
                startingFogDistance          = waterFogDistance;
                startingNextWaterFogDistance = nextWaterFogDistance;
                startingTime                 = Util.getMeasuringTimeMs();
                transitioning                = true;
            }

            float time       = MathHelper.clamp((float) (Util.getMeasuringTimeMs() - startingTime) / TRANSITION_TIME_MS, 0.0f, 1.0f);
            waterFogDistance = MathHelper.lerp(easeInOut(time), startingFogDistance, nextWaterFogDistance);
        } else {
            transitioning    = false;
        }

        RenderSystem.setShaderFogEnd(waterFogDistance - (clientPlayerEntity.getUnderwaterVisibility() * clientPlayerEntity.getUnderwaterVisibility() * 0.03F));
    }



    // Smooth animation for water fog distance transition.
    private static float easeInOut(float t) {
        return MathHelper.lerp(t, t * t, 2 * t - (t * t));
    }



    @Deprecated
    private static float getModifiedFogModeEXP2Distance(int distance) {
        // for information on the 'glFogf' OpenGL function: https://docs.microsoft.com/en-us/windows/win32/opengl/glfogf
        return (MathHelper.sqrt((float) Math.log(distance))/distance) + 0.03f;
    }



    private static float getWaterFogDistance(Biome biome) {
        // We leave things simple in case of modded biomes.
        float fogDistance = switch (biome.getCategory()) {
            case SWAMP -> SWAMP_BIOME_FOG_DISTANCE;
            case RIVER -> biome.getTemperature() < 0.2f ? FROZEN_RIVER_BIOME_FOG_DISTANCE : RIVER_BIOME_FOG_DISTANCE;
            case BEACH -> biome.getTemperature() < 0.2f ? BEACH_BIOME_FOG_DISTANCE : SNOWY_BEACH_BIOME_FOG_DISTANCE;
            case OCEAN -> OCEAN_BIOME_FOG_DISTANCE;
            default    -> DEFAULT_FOG_DISTANCE;
        };

        // Practically, the distance the fog ends is ~1 block ahead of where it should be. We need to correct for that.
        return fogDistance + 1;
    }
}