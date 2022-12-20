package retr0.bedrockwaters.util;

import net.fabricmc.fabric.api.resource.ModResourcePack;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Identifier;
import retr0.bedrockwaters.BedrockWaters;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static net.fabricmc.fabric.api.resource.ResourcePackActivationType.DEFAULT_ENABLED;
import static net.minecraft.resource.ResourceType.CLIENT_RESOURCES;
import static retr0.bedrockwaters.BedrockWaters.LOGGER;
import static retr0.bedrockwaters.BedrockWaters.MOD_ID;

public class ResourceManager {
    private static final List<Identifier> RESOURCE_IDS =
        Stream.of("textures/block/water_still.png", "textures/block/water_flow.png")
            .map(path -> new Identifier(Identifier.DEFAULT_NAMESPACE, path)).toList();

    private static boolean areModResourcesLoaded = false;

    public static boolean areModResourcesLoaded() {
        // Since Java Edition water textures' alpha values determine the surface opacity of the water, the
        // biome-dependent water opacity feature should be disabled if any resource packs that override the
        // BedrockWaters' water textures is present.
        return areModResourcesLoaded;
    }



    public static void register() {
        // Loading the BedrockWater's default resource pack.
        FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent(modContainer ->
            ResourceManagerHelper.registerBuiltinResourcePack(
                new Identifier(MOD_ID, "resources"), modContainer, DEFAULT_ENABLED)
        );

        // Register a resource reload listener for determining whether the mod's assets have been overwritten or not.
        ResourceManagerHelper.get(CLIENT_RESOURCES).registerReloadListener(
            new SimpleSynchronousResourceReloadListener() {
                private boolean doesPackHaveResources(ResourcePack resourcePack) {
                    var hasResources = new AtomicBoolean(false);

                    if (resourcePack.getNamespaces(CLIENT_RESOURCES).contains(Identifier.DEFAULT_NAMESPACE)) {
                        resourcePack.findResources(CLIENT_RESOURCES, Identifier.DEFAULT_NAMESPACE, "textures/block",
                            (id, supplier) -> {
                                if (RESOURCE_IDS.contains(id)) hasResources.set(true);
                            });
                    }
                    return hasResources.get();
                }

                @Override
                public void reload(net.minecraft.resource.ResourceManager manager) {
                    var modResourcesLoaded = false;

                    // We consider that the mod's assets are loaded if these conditions are met:
                    //   * BedrockWaters resource pack is loaded.
                    //   * The BedrockWaters resource pack does not have its assets overwritten by another pack.
                    try (var packStream = manager.streamResourcePacks()) {
                        for (var resourcePack : packStream.toList()) {
                            LOGGER.info(resourcePack.getName());
                            if (resourcePack instanceof ModResourcePack modResourcePack &&
                                modResourcePack.getFabricModMetadata().getId().equals(MOD_ID))
                            {
                                modResourcesLoaded = true;
                            } else if (modResourcesLoaded && doesPackHaveResources(resourcePack)) {
                                modResourcesLoaded = false;
                                break;
                            }
                        }
                        areModResourcesLoaded = modResourcesLoaded;
                    } catch (Exception e) {
                        BedrockWaters.LOGGER.error("Error occurred while processing resource packs: ", e);
                    }
                }

                @Override
                public Identifier getFabricId() { return new Identifier(MOD_ID, "resource_listener"); }
            });
    }
}
