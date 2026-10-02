package team.lodestar.lodestone.internal.worldevent;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import team.lodestar.lodestone.modules.toolkit.worldevent.WorldEventType;

import java.util.List;
import java.util.Objects;

public final class LodestoneWorldEventRegistry {
    public static final ResourceKey<Registry<WorldEventType>> KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("lodestone", "world_event_type"));
    private static volatile Registry<WorldEventType> registry;

    private LodestoneWorldEventRegistry() {
    }

    public static synchronized void install(Registry<WorldEventType> value) {
        if (registry != null) {
            throw new IllegalStateException("The Lodestone world event registry has already been installed");
        }
        registry = Objects.requireNonNull(value);
    }

    public static Registry<WorldEventType> registry() {
        Registry<WorldEventType> value = registry;
        if (value == null) {
            throw new IllegalStateException("The Lodestone world event registry has not been installed");
        }
        return value;
    }

    public static List<WorldEventType> getEventTypes() {
        return registry().stream().toList();
    }
}
