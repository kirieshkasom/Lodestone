package team.lodestar.lodestone.modules.rendering.particle.pooled.visual.types.billboard;

import team.lodestar.lodestone.modules.rendering.particle.pooled.runtime.ParticleView;
import team.lodestar.lodestone.modules.rendering.particle.pooled.visual.ParticleVisualDrawData;

public class BillboardVisualDrawData implements ParticleVisualDrawData {
    private final ParticleView particles;
    private final int liveCount;
    private final int targetVisualId;

    public BillboardVisualDrawData(ParticleView particles, int liveCount,  int targetVisualId) {
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