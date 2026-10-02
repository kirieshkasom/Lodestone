package team.lodestar.lodestone.modules.rendering.particle.pooled.builder;

import team.lodestar.lodestone.modules.rendering.particle.pooled.component.ParticleComponentType;
import team.lodestar.lodestone.modules.rendering.particle.pooled.visual.ParticleVisualEntry;
import team.lodestar.lodestone.modules.rendering.particle.pooled.visual.ParticleVisualType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ParticleBuilder {
    private final Map<ParticleComponentType<?>, Object> componentConfigs = new LinkedHashMap<>();
    private final List<ParticleVisualEntry<?>> visualEntries = new ArrayList<>();

    private ParticleBuilder() {
    }

    public static ParticleBuilder create() {
        return new ParticleBuilder();
    }

    public <T> ParticleBuilder with(ParticleComponentType<T> type, Consumer<T> configurer) {
        T config = type.configFactory().get();
        configurer.accept(config);
        componentConfigs.put(type, config);
        return this;
    }

    // Evil Java doesnt like overloading generic methods with consumers >:(
    public <T> ParticleBuilder withConfig(ParticleComponentType<T> type, T config) {
        componentConfigs.put(type, config);
        return this;
    }

    public <T> ParticleBuilder withVisual(ParticleVisualType<T> type, Consumer<T> configurer) {
        T config = type.configFactory().get();
        configurer.accept(config);
        visualEntries.add(new ParticleVisualEntry<>(type, config));
        return this;
    }

    public <T> ParticleBuilder withVisualConfig(ParticleVisualType<T> type, T config) {
        visualEntries.add(new ParticleVisualEntry<>(type, config));
        return this;
    }

    public ParticleSpec build() {
        return new ParticleSpec(componentConfigs, visualEntries);
    }
}