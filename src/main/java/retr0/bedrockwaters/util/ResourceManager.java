package retr0.bedrockwaters.util;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ModResourcePack;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
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

    private static final Identifier MOD_RESOURCE_PACK_ID = new Identifier(MOD_ID, "resources");

    private static boolean areModResourcesLoaded = false;

    public static boolean areModResourcesLoaded() {
        // Since Java Edition water textures' alpha values determine the surface opacity of the water, the
        // biome-dependent water opacity feature should be disabled if any resource packs that override the
        // BedrockWaters' water textures is present.
        return areModResourcesLoaded;
    }



    public static void init() {
        // Register default resource pack.
        FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent(modContainer ->
            ResourceManagerHelper.registerBuiltinResourcePack(MOD_RESOURCE_PACK_ID, modContainer, DEFAULT_ENABLED));

        // Register a resource reload listener for determining whether the mod's assets have been overwritten or not.
        ResourceManagerHelper.get(CLIENT_RESOURCES).registerReloadListener(
            new IdentifiableResourceReloadListener() {
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
                public CompletableFuture<Void> reload(
                        Synchronizer synchronizer, net.minecraft.resource.ResourceManager manager, Profiler prepareProfiler,
                        Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor)
                {
                    var packManager = MinecraftClient.getInstance().getResourcePackManager();
                    var modResourcePackId = MOD_RESOURCE_PACK_ID.toString();

                    return CompletableFuture.runAsync(() -> {
                        if (!FabricLoader.getInstance().isModLoaded("sodium")) return;

                        // Disable resource pack if Sodium is loaded and forcefully reload resources.
                        if (manager.streamResourcePacks().anyMatch(pack -> pack.getName().equals(modResourcePackId))) {
                            packManager.disable(modResourcePackId);
                            MinecraftClient.getInstance().reloadResources();
                            LOGGER.warn("Dynamic water opacity is incompatible with Sodium! Mod resource pack will be disabled!");
                        }
                    }, prepareExecutor).thenAcceptAsync(empty -> {
                        if (FabricLoader.getInstance().isModLoaded("sodium")) return;

                        // We consider that the mod's assets are loaded if these conditions are met:
                        //   * BedrockWaters resource pack is loaded.
                        //   * The BedrockWaters resource pack does not have any assets overwritten by another pack.
                        try (var packStream = manager.streamResourcePacks()) {
                            for (var resourcePack : packStream.toList()) {
                                if (resourcePack instanceof ModResourcePack pack && pack.getFabricModMetadata().getId().equals(MOD_ID)) {
                                    areModResourcesLoaded = true;
                                } else if (areModResourcesLoaded && doesPackHaveResources(resourcePack)) {
                                    areModResourcesLoaded = false;

                                    if (resourcePack.getName().isEmpty())
                                        LOGGER.warn("Default resource pack is currently unloaded! Dynamic water opacity will be disabled!");
                                    else
                                        LOGGER.warn("Default resources were overwritten by resource pack named {}! Dynamic water opacity will be disabled!", resourcePack.getName());
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            LOGGER.error("Error while processing resource packs: ", e);
                        }
                    }, applyExecutor).thenCompose(empty -> synchronizer.whenPrepared(null));
                }

                @Override
                public Identifier getFabricId() {
                    return new Identifier(MOD_ID, "resource_listener");
                }
            });
    }
}
