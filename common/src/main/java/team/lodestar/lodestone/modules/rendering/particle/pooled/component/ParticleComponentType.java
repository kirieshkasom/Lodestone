package team.lodestar.lodestone.modules.rendering.particle.pooled.component;

import net.minecraft.resources.ResourceLocation;
import team.lodestar.lodestone.modules.rendering.particle.pooled.runtime.ParticlePhase;
import team.lodestar.lodestone.modules.rendering.particle.pooled.storage.ParticleComponentStorage;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class ParticleComponentType<T> {
    private final ResourceLocation id;
    private final Supplier<T> configFactory;
    private final Function<Integer, ParticleComponentStorage<T>> storageFactory;
    private final EnumSet<ParticlePhase> phases;
    private final int priority;

    private ParticleComponentType(Builder<T> builder) {
        this.id = Objects.requireNonNull(builder.id);
        this.configFactory = Objects.requireNonNull(builder.configFactory);
        this.storageFactory = Objects.requireNonNull(builder.storageFactory);
        this.phases = builder.phases.clone();
        this.priority = builder.priority;
    }

    public ResourceLocation id() {
        return id;
    }

    public Supplier<T> configFactory() {
        return configFactory;
    }

    public ParticleComponentStorage<T> createStorage(int capacity) {
        return storageFactory.apply(capacity);
    }

    public EnumSet<ParticlePhase> phases() {
        return phases.clone();
    }

    public int priority() {
        return priority;
    }

    public static <T> Builder<T> builder(ResourceLocation id) {
        return new Builder<>(id);
    }

    public static final class Builder<T> {
        private final ResourceLocation id;
        private Supplier<T> configFactory;
        private Function<Integer, ParticleComponentStorage<T>> storageFactory;
        private EnumSet<ParticlePhase> phases = EnumSet.noneOf(ParticlePhase.class);
        private int priority = 0;

        private Builder(ResourceLocation id) {
            this.id = id;
        }

        public Builder<T> configFactory(Supplier<T> configFactory) {
            this.configFactory = configFactory;
            return this;
        }

        public Builder<T> storageFactory(Function<Integer, ParticleComponentStorage<T>> storageFactory) {
            this.storageFactory = storageFactory;
            return this;
        }

        public Builder<T> phases(ParticlePhase... phases) {
            this.phases = EnumSet.noneOf(ParticlePhase.class);
            this.phases.addAll(Arrays.asList(phases));
            return this;
        }

        public Builder<T> priority(int priority) {
            this.priority = priority;
            return this;
        }

        public ParticleComponentType<T> build() {
            return new ParticleComponentType<>(this);
        }
    }
}
