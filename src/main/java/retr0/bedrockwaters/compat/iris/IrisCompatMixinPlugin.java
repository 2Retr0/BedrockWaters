package retr0.bedrockwaters.compat.iris;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import retr0.bedrockwaters.compat.AbstractMixinPlugin;

public class IrisCompatMixinPlugin extends AbstractMixinPlugin {
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        var loader = FabricLoader.getInstance();
        var isClient = loader.getEnvironmentType() == EnvType.CLIENT;

        return isClient && loader.isModLoaded("iris"); // With transitive Sodium dependency.
    }
}
