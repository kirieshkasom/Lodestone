package team.lodestar.lodestone.modules.rendering.particle.pooled.visual;

@FunctionalInterface
public interface ParticleVisualRuntimeFactory<T> {
    ParticleVisualRuntime create(T config);
}