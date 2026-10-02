package team.lodestar.lodestone.modules.rendering.particle.pooled.component;

import team.lodestar.lodestone.modules.rendering.particle.pooled.runtime.ParticleView;

public interface PostUpdateComponent {
    void postUpdate(int liveCount, float dt, ParticleView particles);
}
