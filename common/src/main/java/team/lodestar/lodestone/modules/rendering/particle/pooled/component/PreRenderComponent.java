package team.lodestar.lodestone.modules.rendering.particle.pooled.component;

import team.lodestar.lodestone.modules.rendering.particle.pooled.runtime.ParticleView;

public interface PreRenderComponent {
    void preRender(int liveCount, ParticleView particles, float partialTicks);
}