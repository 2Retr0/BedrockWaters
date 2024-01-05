package retr0.bedrockwaters.mixin;

import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.BackgroundRenderer.FogData;
import net.minecraft.client.render.BackgroundRenderer.FogType;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import net.minecraft.client.render.FogShape;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import retr0.bedrockwaters.config.BedrockWatersConfig;
import retr0.bedrockwaters.event.ClientPlayerEntityEvents;
import retr0.bedrockwaters.util.SmoothStepUtil;
import retr0.bedrockwaters.util.WaterPropertiesManager;

import static net.minecraft.client.render.CameraSubmersionType.WATER;
import static retr0.bedrockwaters.util.WaterPropertiesManager.DEFAULT_BEDROCK_PROPERTIES;

@Mixin(BackgroundRenderer.class)
public abstract class MixinBackgroundRenderer {
    @Unique private static final float TRANSITION_TIME_MS = 5000f;

    // A SmoothStepUtil instance for transitioning between biome distances while underwater.
    @Unique private static SmoothStepUtil biomeStep = new SmoothStepUtil(DEFAULT_BEDROCK_PROPERTIES.waterFogDistance());
    // A SmoothStepUtil instance for transitioning from a 25-100% distance multiplier when submerging underwater.
    @Unique private static SmoothStepUtil underwaterStep = new SmoothStepUtil(1);

    @Unique private static int targetFogColor = DEFAULT_BEDROCK_PROPERTIES.waterFogColor();
    @Unique private static float targetFogDistance = DEFAULT_BEDROCK_PROPERTIES.waterFogDistance();

    static {
        // Whenever the player crosses over to a new biome, we alter the target color and distance to match the biome.
        ClientPlayerEntityEvents.BIOME_CHANGED.register(((clientPlayerEntity, biome) -> {
            var properties = WaterPropertiesManager.getWaterProperties(biome);

            targetFogColor = properties.waterFogColor();
            targetFogDistance = properties.waterFogDistance();

            // If the player is above water, we also 'complete' the fog distance transition.
            if (!clientPlayerEntity.isSubmergedInWater())
                biomeStep = new SmoothStepUtil(targetFogDistance);
        }));

        // When the player initially submerges underwater, we set a five-second underwater visibility transition
        // (following a three-second delay) transitioning from 25-100% the biome's fog distance. This mirrors the
        // behavior present in Bedrock Edition.
        ClientPlayerEntityEvents.START_SUBMERGE.register((clientPlayerEntity) ->
            underwaterStep = new SmoothStepUtil(0.25f, 1f, TRANSITION_TIME_MS, 3000f)
        );

        // When the player exits the water, we 'complete' the fog distance transition and 'reset' the underwater
        // visibility transition.
        ClientPlayerEntityEvents.END_SUBMERGE.register((clientPlayerEntity) -> {
            biomeStep = new SmoothStepUtil(targetFogDistance);
            underwaterStep = new SmoothStepUtil(0.25f);
        });
    }


    /**
     * Returns the Bedrock Edition water color instead of vanilla. Assumes that the target biome is the biome that the
     * player currently resides in.
     */
    @SuppressWarnings("InvalidInjectorMethodSignature")
    @ModifyVariable(
        method = "render",
        at = @At(
            value = "INVOKE_ASSIGN",
            target = "Lnet/minecraft/world/biome/Biome;getWaterFogColor()I"),
        ordinal = 1
    )
    private static int modifyWaterFogColor(int original) {
        return targetFogColor;
    }



    /**
     * Patches the water fog distances for various biomes and introduces a smooth transition between fog distances.
     */
    @Inject(
        method = "applyFog",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderFogStart(F)V"),
        locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private static void onApplyFogUnderWater(
        Camera camera, FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci,
        CameraSubmersionType cameraSubmersionType, Entity entity, FogData fogData)
    {
        if (!BedrockWatersConfig.enableAlternateFog) return;

        if (cameraSubmersionType == WATER) {
            var currentFogDistance = biomeStep.currentValue();

            // If, while underwater, the biome changes, we transition from the current fog distance to the new
            // target fog distance.
            if (targetFogDistance != biomeStep.targetValue())
                biomeStep = new SmoothStepUtil(currentFogDistance, targetFogDistance, TRANSITION_TIME_MS);

            // Vanilla has a slower, more subtle underwater visibility adjustment system--we will incorporate this
            // into our new visibility calculations.
            fogData.fogEnd = currentFogDistance * underwaterStep.currentValue();

            // Ensure that the max fog distance lies within the client's view distance when underwater.
            if (fogData.fogEnd > viewDistance) {
                fogData.fogEnd = viewDistance;
                fogData.fogShape = FogShape.CYLINDER;
            }
        }
    }
}
