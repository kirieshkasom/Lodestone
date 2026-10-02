package team.lodestar.lodestone.modules.rendering.particle.pooled.visual;

import java.util.Objects;

public class ParticleVisualEntry<T> {
    private final ParticleVisualType<T> type;
    private final T config;

    public ParticleVisualEntry(ParticleVisualType<T> type, T config) {
        this.type = Objects.requireNonNull(type, "type");
        this.config = Objects.requireNonNull(config, "config");
    }

    public ParticleVisualType<T> type() {
        return type;
    }

    public T config() {
        return config;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParticleVisualEntry<?> that)) return false;
        return type.equals(that.type) && config.equals(that.config);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, config);
    }
}