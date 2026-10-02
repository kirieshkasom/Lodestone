package team.lodestar.lodestone.internal.registration;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public interface RegistryRegistrar<R> {
    <T extends R> Supplier<T> register(ResourceLocation id, Supplier<T> factory);

    default <T extends R> Supplier<T> register(RegistryEntry<T> entry) {
        entry.ensureUnbound();
        Supplier<T> value = register(entry.id(), entry.factory());
        entry.bind(value);
        return value;
    }
}
