package team.lodestar.lodestone.modules.rendering.particle.pooled.visual;

import team.lodestar.lodestone.modules.rendering.particle.pooled.runtime.ParticleView;

public class ParticleVisualCollectContext {
    private final ParticleVisualCollector collector;
    private final ParticleView particles;
    private final int targetVisualId;

    public ParticleVisualCollectContext(ParticleVisualCollector collector, ParticleView particles, int targetVisualId) {
        this.collector = collector;
        this.particles = particles;
        this.targetVisualId = targetVisualId;
    }

    public ParticleVisualCollector collector() {
        return collector;
    }

    public ParticleView particles() {
        return particles;
    }

    public int targetVisualId() {
        return targetVisualId;
    }
}