package retr0.bedrockwaters.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.FluidRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

import static retr0.bedrockwaters.BedrockWaters.MOD_ID;

@Mixin(FluidRenderer.class)
public abstract class FluidRendererMixin {
    @Shadow @Final private Sprite[] waterSprites;

    private final Identifier bedrockWaterStillSpriteId = new Identifier(MOD_ID, "block/water_still");
    private final Identifier bedrockWaterFlowSpriteId = new Identifier(MOD_ID, "block/water_flow");

    @Inject(method = "onResourceReload()V", at = @At("TAIL"))
    private void onResourceReload(CallbackInfo ci) {
        final Function<Identifier, Sprite> atlas = MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);

        this.waterSprites[0] = atlas.apply(bedrockWaterStillSpriteId);
        this.waterSprites[1] = atlas.apply(bedrockWaterFlowSpriteId);

        //System.out.println(MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(Blocks.WATER.getDefaultState()).getSprite().toString());
    }
}
