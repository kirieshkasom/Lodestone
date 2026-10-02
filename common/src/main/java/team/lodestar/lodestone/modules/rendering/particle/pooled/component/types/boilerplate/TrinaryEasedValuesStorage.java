package team.lodestar.lodestone.modules.rendering.particle.pooled.component.types.boilerplate;

import team.lodestar.lodestone.modules.core.easing.*;
import team.lodestar.lodestone.modules.rendering.particle.pooled.component.*;
import team.lodestar.lodestone.modules.rendering.particle.pooled.component.types.*;
import team.lodestar.lodestone.modules.rendering.particle.pooled.runtime.*;
import team.lodestar.lodestone.modules.rendering.particle.pooled.storage.*;

public abstract class TrinaryEasedValuesStorage<T extends ITrinaryConfig> implements ParticleComponentStorage<T>, PreRenderComponent {

    private final ConstantLerpOrDoubleLerp[] mode;
    private final float[] s0, s1, s2;

    private final Easing[] easing0;
    private final Easing[] easing1;

    public TrinaryEasedValuesStorage(int capacity) {
        this.mode = new ConstantLerpOrDoubleLerp[capacity];
        this.s0 = new float[capacity];

        this.s1 = new float[capacity];
        this.s2 = new float[capacity];

        this.easing0 = new Easing[capacity];
        this.easing1 = new Easing[capacity];
    }

    @Override
    public void preRender(int liveCount, ParticleView particles, float partialTicks) {
        int[] age = particles.age();
        int[] lifetime = particles.lifetime();

        for (int i = 0; i < liveCount; i++) {

            switch (mode[i]) {
                case CONSTANT -> {
                    float value = s0[i];
                    acceptValue(liveCount, particles, i, value);
                }
                case LERP -> {
                    float t = lifetime[i] <= 0 ? 1.0f : (age[i]+partialTicks) / (float) lifetime[i];
                    t = easing0[i].ease(t);
                    float value = lerp(s0[i], s1[i], t);
                    acceptValue(liveCount, particles, i, value);
                }
                case DOUBLE_LERP -> {
                    float t = lifetime[i] <= 0 ? 1.0f : (age[i]+partialTicks) / (float) lifetime[i];
                    if (t <= 0.5f) {
                        t = easing0[i].ease(t*2);
                        float value = lerp(s0[i], s1[i], t);
                        acceptValue(liveCount, particles, i, value);
                    }
                    else {
                        t = easing1[i].ease((t-0.5f)*2);
                        float value = lerp(s1[i], s2[i], t);
                        acceptValue(liveCount, particles, i, value);
                    }
                }
            }
        }
    }

    protected abstract void acceptValue(int liveCount, ParticleView particles, int particleIndex, float value);

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    @Override
    public void onSpawn(int particleIndex, T config, ParticleSpawnContext spawnContext, ParticleView particles) {
        mode[particleIndex] = config.getMode();

        float[] values = config.getValues();
        var easings = config.getEasings();
        s0[particleIndex] = values[0];
        s1[particleIndex] = values[1];
        s2[particleIndex] = values[2];

        easing0[particleIndex] = easings[0];
        easing1[particleIndex] = easings[1];
    }

    @Override
    public void onSwapRemove(int deadIndex, int movedIndex, ParticleView particles) {
        mode[deadIndex] = mode[movedIndex];

        s0[deadIndex] = s0[movedIndex];
        s1[deadIndex] = s1[movedIndex];
        s2[deadIndex] = s2[movedIndex];

        easing0[deadIndex] = easing0[movedIndex];
        easing1[deadIndex] = easing1[movedIndex];
    }
}
