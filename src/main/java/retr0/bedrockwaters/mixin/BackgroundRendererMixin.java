package retr0.bedrockwaters.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import retr0.bedrockwaters.WaterPropertiesReplacer;

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
    private static Entity entity;
    private static RegistryEntry<Biome> biome;
    private static float startingFogDistance;
    private static float startingNextWaterFogDistance;
    private static long startingTime;

    @ModifyVariable(
            method = "render(Lnet/minecraft/client/render/Camera;FLnet/minecraft/client/world/ClientWorld;IF)V",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/biome/Biome;getWaterFogColor()I"),
            ordinal = 1, index = 9
    )
    private static int modifyWaterFogColor(int original) {
        if (biome == null) return original;

        return WaterPropertiesReplacer.getBiomeWaterProperties(biome, true);
    }



    @Inject(
        method = "applyFog(Lnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/BackgroundRenderer$FogType;FZ)V",
        at = @At(
            value   = "INVOKE",
            target  = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderFogEnd(F)V",
            ordinal = 0,
            shift   = At.Shift.AFTER
        ),
        cancellable = true
    )
    private static void onApplyFogUnderWater(
        Camera camera,
        BackgroundRenderer.FogType fogType,
        float viewDistance,
        boolean thickFog,
        CallbackInfo ci
    ) {
        /* Cancel if the entity is not a LivingEntity or if the entity has the blindness effect.
         * There is an in-game oversight(?) which allows normal visibility underwater even with the blindness effect.
         * Shall leave this issue be for now.
         */
        if (!(entity instanceof LivingEntity) || ((LivingEntity)entity).hasStatusEffect(StatusEffects.BLINDNESS)) ci.cancel();

        entity                           = camera.getFocusedEntity();
        biome                            = entity.world.getBiome(entity.getBlockPos());
        float nextWaterFogDistance       = getWaterFogDistance(biome);
        float playerUnderwaterVisibility = 0;

        CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();

        if (cameraSubmersionType == CameraSubmersionType.WATER) {
            // Vanilla has a slower, more subtle underwater visibility adjustment system. We will incorporate this into our new
            // visibility calculations if the entity is a ClientPlayerEntity.
            if (entity instanceof ClientPlayerEntity)
                playerUnderwaterVisibility = ((ClientPlayerEntity) entity).getUnderwaterVisibility() * ((ClientPlayerEntity) entity).getUnderwaterVisibility() * 0.03F;

            // Underwater visibility calculations.
            if (MathHelper.abs(waterFogDistance - nextWaterFogDistance) > 0.0002) {
                // If fog distance is not transitioning or if the fog distance needs to re-transition.
                if (!transitioning || startingNextWaterFogDistance != nextWaterFogDistance) {
                    // "freeze" the value of variables for time and linear interpolation calculations.
                    startingFogDistance = waterFogDistance;
                    startingNextWaterFogDistance = nextWaterFogDistance;
                    startingTime = Util.getMeasuringTimeMs();
                    transitioning = true;
                }

                float time = MathHelper.clamp((float) (Util.getMeasuringTimeMs() - startingTime) / TRANSITION_TIME_MS, 0.0f, 1.0f);
                waterFogDistance = MathHelper.lerp(easeInOut(time), startingFogDistance, nextWaterFogDistance);
            } else {
                transitioning = false;
            }

            RenderSystem.setShaderFogEnd(waterFogDistance - playerUnderwaterVisibility);
        } else {
            if (entity instanceof LivingEntity) {
                biome            = entity.world.getBiome(entity.getBlockPos());
                waterFogDistance = getWaterFogDistance(biome);
            }
        }
    }



    // Smooth animation for water fog distance transition.
    private static float easeInOut(float t) {
        return MathHelper.lerp(t, t * t, 2 * t - (t * t));
    }



    private static float getWaterFogDistance(RegistryEntry<Biome> biome) {
        // We leave things simple in case of modded biomes.
        float fogDistance = switch (((BiomeInvoker)(Object) biome.value()).invokeGetCategory()) {
            case SWAMP -> SWAMP_BIOME_FOG_DISTANCE;
            case RIVER -> biome.value().getTemperature() < 0.2f ? FROZEN_RIVER_BIOME_FOG_DISTANCE : RIVER_BIOME_FOG_DISTANCE;
            case BEACH -> biome.value().getTemperature() < 0.2f ? BEACH_BIOME_FOG_DISTANCE : SNOWY_BEACH_BIOME_FOG_DISTANCE;
            case OCEAN -> OCEAN_BIOME_FOG_DISTANCE;
            default    -> DEFAULT_FOG_DISTANCE;
        };

        // Practically, the distance the fog ends is ~1 block ahead of where it should be. We need to correct for that.
        return fogDistance + 1;
    }
}