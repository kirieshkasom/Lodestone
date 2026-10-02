package team.lodestar.lodestone.modules.rendering.particle.pooled.visual.types.quad;

import team.lodestar.lodestone.modules.rendering.particle.pooled.runtime.ParticleView;
import team.lodestar.lodestone.modules.rendering.particle.pooled.visual.ParticleVisualDrawData;

public class QuadVisualDrawData implements ParticleVisualDrawData {
    private final ParticleView particles;
    private final int liveCount;
    private final int targetVisualId;

    public QuadVisualDrawData(ParticleView particles, int liveCount, int targetVisualId) {
        this.particles = particles;
        this.liveCount = liveCount;
        this.targetVisualId = targetVisualId;
    }

    public ParticleView particles() {
        return particles;
    }

    public int liveCount() {
        return liveCount;
    }

    public int targetVisualId() {
        return targetVisualId;
    }
}