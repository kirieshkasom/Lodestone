package team.lodestar.lodestone.modules.rendering.particle.pooled.component.types.spin;

import team.lodestar.lodestone.modules.rendering.particle.pooled.component.*;
import team.lodestar.lodestone.modules.rendering.particle.pooled.component.types.boilerplate.*;
import team.lodestar.lodestone.modules.rendering.particle.pooled.runtime.*;

public class SpinStorage extends TrinaryEasedValuesStorage<SpinConfig> implements PreRenderComponent {

    public SpinStorage(int capacity) {
        super(capacity);
    }

    @Override
    protected void acceptValue(int liveCount, ParticleView particles, int particleIndex, float value) {
        particles.spin()[particleIndex] += value;
    }
}
