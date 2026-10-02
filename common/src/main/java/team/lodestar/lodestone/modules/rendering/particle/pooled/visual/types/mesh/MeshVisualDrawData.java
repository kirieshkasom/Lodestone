package team.lodestar.lodestone.modules.rendering.particle.pooled.visual.types.mesh;

import team.lodestar.lodestone.modules.rendering.particle.pooled.runtime.ParticleView;
import team.lodestar.lodestone.modules.rendering.particle.pooled.visual.ParticleVisualDrawData;

public class MeshVisualDrawData implements ParticleVisualDrawData {
    private final ParticleView particles;
    private final int liveCount;
    private final int targetVisualId;
    private final float offsetX;
    private final float offsetY;
    private final float offsetZ;

    public MeshVisualDrawData(ParticleView particles, int liveCount, int targetVisualId, float offsetX, float offsetY, float offsetZ) {
        this.particles = particles;
        this.liveCount = liveCount;
        this.targetVisualId = targetVisualId;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
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

    public float offsetX() {
        return offsetX;
    }

    public float offsetY() {
        return offsetY;
    }

    public float offsetZ() {
        return offsetZ;
    }
}