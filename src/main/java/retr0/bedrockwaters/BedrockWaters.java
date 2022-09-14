package retr0.bedrockwaters;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

import static net.fabricmc.fabric.api.resource.ResourcePackActivationType.DEFAULT_ENABLED;
import static net.minecraft.resource.ResourceType.CLIENT_RESOURCES;

public class BedrockWaters implements ClientModInitializer {
    public static final String MOD_ID = "bedrockwaters";
    public static final String MOD_NAME = "BedrockWaters";

    public static Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static boolean areAssetsLoaded;

    @Override
    public void onInitializeClient() {
        LOGGER.info(MOD_NAME + " initialized!");

        // Loading the BedrockWater's default resource pack.
        FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent(modContainer ->
            ResourceManagerHelper.registerBuiltinResourcePack(
                    new Identifier(MOD_ID, "resources"), modContainer, DEFAULT_ENABLED)
        );

        // Register a resource reload listener for determining whether the mod's assets have been overwritten or not.
        ResourceManagerHelper.get(CLIENT_RESOURCES).registerReloadListener(
            new SimpleSynchronousResourceReloadListener() {
                private boolean packHasAssets(ResourcePack pack) {
                    var assetIds = Stream.of("water_flow.png", "water_still.png")
                        .map(n -> new Identifier("textures/block/" + n));

                    return assetIds.anyMatch(id -> pack.contains(CLIENT_RESOURCES, id));
                }

                @Override
                public Identifier getFabricId() { return new Identifier(MOD_ID, "resources"); }


                @Override
                public void reload(ResourceManager manager) {
                    areAssetsLoaded = false;

                    // We consider that the mod's assets are loaded if these conditions are met:
                    //   * BedrockWaters resource pack is loaded.
                    //   * The BedrockWaters resource pack does not have its assets overwritten by another pack.
                    try (var packStream = manager.streamResourcePacks()) {
                        packStream.forEachOrdered(pack -> {
                            if (pack.getName().contains(MOD_ID))
                                areAssetsLoaded = true;
                            else if (areAssetsLoaded && packHasAssets(pack))
                                areAssetsLoaded = false;
                        });
                    } catch (Exception e) {
                        LOGGER.error("Error occurred while processing resource packs: ", e);
                    }
                }
            });
    }
}
