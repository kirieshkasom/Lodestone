package team.lodestar.lodestone.modules.rendering.particle.pooled.visual.types.trail;

import team.lodestar.lodestone.modules.rendering.particle.pooled.runtime.ParticleView;
import team.lodestar.lodestone.modules.rendering.particle.pooled.visual.ParticleVisualDrawData;

public class TrailVisualDrawData implements ParticleVisualDrawData {
    private final ParticleView particles;
    private final TrailVisualStorage storage;
    private final int liveCount;
    private final int targetVisualId;
    private final float width;

    public TrailVisualDrawData(ParticleView particles, TrailVisualStorage storage, int liveCount, int targetVisualId, float width) {
        this.particles = particles;
        this.storage = storage;
        this.liveCount = liveCount;
        this.targetVisualId = targetVisualId;
        this.width = width;
    }

    public ParticleView particles() {
        return particles;
    }

    public TrailVisualStorage storage() {
        return storage;
    }

    public int liveCount() {
        return liveCount;
    }

    public int targetVisualId() {
        return targetVisualId;
    }

    public float width() {
        return width;
    }
}