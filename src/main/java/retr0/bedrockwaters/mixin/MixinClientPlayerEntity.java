package retr0.bedrockwaters.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import retr0.bedrockwaters.ClientPlayerEntityEvents;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity {
    @Unique private RegistryEntry<Biome> previousBiome;

    /**
     * Handles conditions for invoking the {@link ClientPlayerEntityEvents#BIOME_CHANGED} event.
     */
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        var biome = this.clientWorld.getBiome(this.getBlockPos());

        // Invoke the BIOME_CHANGED event if the previously saved biome is no longer equal to the current biome.
        if (previousBiome != biome) {
            ClientPlayerEntityEvents.BIOME_CHANGED.invoker().onBiomeChanged((ClientPlayerEntity) (Object) this, biome);
            previousBiome = biome;
        }
    }



    /**
     * Handles conditions for invoking the {@link ClientPlayerEntityEvents#START_SUBMERGE} event.
     */
    @Inject(
        method = "updateWaterSubmersionState",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;" +
                "playSound(DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZ)V",
            ordinal = 0)
    )
    private void onStartSubmerge(CallbackInfoReturnable<Boolean> cir) {
        ClientPlayerEntityEvents.START_SUBMERGE.invoker().onStartSubmerge((ClientPlayerEntity) (Object) this);
    }



    /**
     * Handles conditions for invoking the {@link ClientPlayerEntityEvents#END_SUBMERGE} event.
     */
    @Inject(
        method = "updateWaterSubmersionState()Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;" +
                "playSound(DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZ)V",
            ordinal = 1)
    )
    private void onEndSubmerge(CallbackInfoReturnable<Boolean> cir) {
        ClientPlayerEntityEvents.END_SUBMERGE.invoker().onEndSubmerge((ClientPlayerEntity) (Object) this);
    }



    private MixinClientPlayerEntity(ClientWorld world, GameProfile profile, @Nullable PlayerPublicKey publicKey) {
        super(world, profile, publicKey);
    }
}
