package team.lodestar.lodestone.modules.rendering.particle.pooled.visual;

import team.lodestar.lodestone.modules.rendering.particle.pooled.pool.ParticlePool;

public interface ParticleVisualRuntime {
    void collect(ParticleVisualCollectContext context, ParticlePool pool, int liveCount);
}