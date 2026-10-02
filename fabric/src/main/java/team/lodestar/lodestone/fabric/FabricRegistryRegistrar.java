package team.lodestar.lodestone.fabric;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import team.lodestar.lodestone.internal.registration.RegistryRegistrar;

import java.util.function.Supplier;

public final class FabricRegistryRegistrar<R> implements RegistryRegistrar<R> {
    private final Registry<R> registry;

    public FabricRegistryRegistrar(Registry<R> registry) {
        this.registry = registry;
    }

    @Override
    public <T extends R> Supplier<T> register(ResourceLocation id, Supplier<T> factory) {
        T value = Registry.register(registry, id, factory.get());
        return () -> value;
    }
}
