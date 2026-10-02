package team.lodestar.lodestone.registry.common;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import team.lodestar.lodestone.internal.worldevent.LodestoneWorldEventRegistry;
import team.lodestar.lodestone.modules.toolkit.worldevent.WorldEventType;

import java.util.List;

public final class LodestoneWorldEventTypes {
    public static final ResourceKey<Registry<WorldEventType>> WORLD_EVENT_TYPE_KEY = LodestoneWorldEventRegistry.KEY;
    public static final Registry<WorldEventType> WORLD_EVENT_TYPE_REGISTRY = createRegistry();

    private static Registry<WorldEventType> createRegistry() {
        Registry<WorldEventType> registry = FabricRegistryBuilder.createSimple(WORLD_EVENT_TYPE_KEY)
                .attribute(RegistryAttribute.SYNCED)
                .buildAndRegister();
        LodestoneWorldEventRegistry.install(registry);
        return registry;
    }

    public static List<WorldEventType> getEventTypes() {
        return LodestoneWorldEventRegistry.getEventTypes();
    }
}
