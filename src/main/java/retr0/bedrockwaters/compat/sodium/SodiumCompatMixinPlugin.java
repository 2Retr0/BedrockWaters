package retr0.bedrockwaters.compat.sodium;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import retr0.bedrockwaters.compat.AbstractMixinPlugin;

public class SodiumCompatMixinPlugin extends AbstractMixinPlugin {
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        var loader = FabricLoader.getInstance();
        var isClient = loader.getEnvironmentType() == EnvType.CLIENT;

        // We only apply if Sodium is loaded and not Iris--Iris redirects to the same invoke and has additional
        // conditions for when we may not want to patch water opacity.
        return isClient && loader.isModLoaded("sodium") && !loader.isModLoaded("iris");
    }
}
