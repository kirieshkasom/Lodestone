package team.lodestar.lodestone.registry.common;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredRegister;
import team.lodestar.lodestone.internal.LodestoneCommon;
import team.lodestar.lodestone.internal.worldevent.LodestoneWorldEventRegistry;
import team.lodestar.lodestone.modules.toolkit.worldevent.WorldEventType;

import java.util.List;

public class LodestoneWorldEventTypes {
    public static final ResourceKey<Registry<WorldEventType>> WORLD_EVENT_TYPE_KEY = LodestoneWorldEventRegistry.KEY;
    public static final DeferredRegister<WorldEventType> WORLD_EVENT_TYPES = createRegistry(LodestoneCommon.LODESTONE);
    public static final Registry<WorldEventType> WORLD_EVENT_TYPE_REGISTRY = createRegistryAndInstall();

    private static Registry<WorldEventType> createRegistryAndInstall() {
        Registry<WorldEventType> registry = WORLD_EVENT_TYPES.makeRegistry(builder -> builder.sync(true));
        LodestoneWorldEventRegistry.install(registry);
        return registry;
    }

    /**
     * Creates a new world event type registry for the given mod ID.
     *
     * @param modId Your mod ID.
     * @return The deferred register for WorldEventType.
     */
    public static DeferredRegister<WorldEventType> createRegistry(String modId) {
        return DeferredRegister.create(WORLD_EVENT_TYPE_KEY, modId);
    }

    public static List<WorldEventType> getEventTypes() {
        return LodestoneWorldEventRegistry.getEventTypes();
    }
}
