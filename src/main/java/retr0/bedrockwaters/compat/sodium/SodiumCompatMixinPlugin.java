package retr0.bedrockwaters.compat.sodium;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import retr0.bedrockwaters.compat.AbstractMixinPlugin;

public class SodiumCompatMixinPlugin extends AbstractMixinPlugin {
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        var loader = FabricLoader.getInstance();
        var isClient = loader.getEnvironmentType() == EnvType.CLIENT;

        return isClient && loader.isModLoaded("sodium");
    }
}
