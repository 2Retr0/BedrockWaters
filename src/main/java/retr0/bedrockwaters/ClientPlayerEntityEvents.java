package retr0.bedrockwaters;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;

@Environment(EnvType.CLIENT)
public final class ClientPlayerEntityEvents {
    /**
     * Callback for when a ClientPlayerEntity crosses a biome in their world.
     */
    public static final Event<BiomeChanged> BIOME_CHANGED = EventFactory.createArrayBacked(BiomeChanged.class,
        callbacks -> (clientPlayerEntity, biome) -> {
            for (BiomeChanged callback : callbacks) {
                callback.onBiomeChanged(clientPlayerEntity, biome);
            }
        });

    /**
     * Callback for when a ClientPlayerEntity crosses a biome in their world.
     */
    public static final Event<StartSubmerge> START_SUBMERGE = EventFactory.createArrayBacked(StartSubmerge.class,
        callbacks -> (clientPlayerEntity) -> {
            for (StartSubmerge callback : callbacks) {
                callback.onStartSubmerge(clientPlayerEntity);
            }
        });

    /**
     * Callback for when a ClientPlayerEntity crosses a biome in their world.
     */
    public static final Event<EndSubmerge> END_SUBMERGE = EventFactory.createArrayBacked(EndSubmerge.class,
        callbacks -> (clientPlayerEntity) -> {
            for (EndSubmerge callback : callbacks) {
                callback.onEndSubmerge(clientPlayerEntity);
            }
        });

    @FunctionalInterface
    public interface BiomeChanged {
        /**
         * @param clientPlayerEntity
         * @param biome
         */
        void onBiomeChanged(ClientPlayerEntity clientPlayerEntity, RegistryEntry<Biome> biome);
    }

    @FunctionalInterface
    public interface StartSubmerge {
        /**
         * @param clientPlayerEntity
         */
        void onStartSubmerge(ClientPlayerEntity clientPlayerEntity);
    }

    @FunctionalInterface
    public interface EndSubmerge {
        /**
         * @param clientPlayerEntity
         */
        void onEndSubmerge(ClientPlayerEntity clientPlayerEntity);
    }

    private ClientPlayerEntityEvents() { }
}
