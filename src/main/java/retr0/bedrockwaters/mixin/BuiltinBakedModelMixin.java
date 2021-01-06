package retr0.bedrockwaters.mixin;

import net.minecraft.client.render.model.BuiltinBakedModel;
import net.minecraft.client.texture.Sprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BuiltinBakedModel.class)
public interface BuiltinBakedModelMixin {
    @Accessor("sprite")
    void setSprite(Sprite sprite);
}