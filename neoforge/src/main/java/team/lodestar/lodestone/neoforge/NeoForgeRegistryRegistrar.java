package team.lodestar.lodestone.neoforge;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import team.lodestar.lodestone.internal.registration.RegistryEntry;
import team.lodestar.lodestone.internal.registration.RegistryRegistrar;

import java.util.function.Supplier;

public final class NeoForgeRegistryRegistrar<R> implements RegistryRegistrar<R> {
    private final DeferredRegister<R> registry;

    public NeoForgeRegistryRegistrar(DeferredRegister<R> registry) {
        this.registry = registry;
    }

    @Override
    public <T extends R> DeferredHolder<R, T> register(ResourceLocation id, Supplier<T> factory) {
        if (!registry.getNamespace().equals(id.getNamespace())) {
            throw new IllegalArgumentException("Unexpected registry namespace: " + id);
        }
        return registry.register(id.getPath(), factory);
    }

    @Override
    public <T extends R> DeferredHolder<R, T> register(RegistryEntry<T> entry) {
        entry.ensureUnbound();
        DeferredHolder<R, T> value = register(entry.id(), entry.factory());
        entry.bind(value);
        return value;
    }
}
