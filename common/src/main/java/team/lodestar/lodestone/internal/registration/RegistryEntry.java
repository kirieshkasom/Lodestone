package team.lodestar.lodestone.internal.registration;

import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.function.Supplier;

public final class RegistryEntry<T> implements Supplier<T> {
    private final ResourceLocation id;
    private final Supplier<T> factory;
    private Supplier<T> value;

    public RegistryEntry(ResourceLocation id, Supplier<T> factory) {
        this.id = Objects.requireNonNull(id);
        this.factory = Objects.requireNonNull(factory);
    }

    public ResourceLocation id() {
        return id;
    }

    public Supplier<T> factory() {
        return factory;
    }

    public void ensureUnbound() {
        if (this.value != null) {
            throw new IllegalStateException("Registry entry already registered: " + id);
        }
    }

    public void bind(Supplier<T> value) {
        ensureUnbound();
        this.value = Objects.requireNonNull(value);
    }

    @Override
    public T get() {
        if (value == null) {
            throw new IllegalStateException("Registry entry accessed before registration: " + id);
        }
        return value.get();
    }
}
