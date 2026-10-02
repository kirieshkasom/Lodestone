package team.lodestar.lodestone.modules.rendering.particle.pooled.component.types.color;

import team.lodestar.lodestone.modules.core.easing.Easing;
import team.lodestar.lodestone.modules.rendering.particle.pooled.component.PreRenderComponent;
import team.lodestar.lodestone.modules.rendering.particle.pooled.component.types.*;
import team.lodestar.lodestone.modules.rendering.particle.pooled.runtime.ParticleSpawnContext;
import team.lodestar.lodestone.modules.rendering.particle.pooled.runtime.ParticleView;
import team.lodestar.lodestone.modules.rendering.particle.pooled.storage.ParticleComponentStorage;

public class ColorStorage implements ParticleComponentStorage<ColorConfig>, PreRenderComponent {
    private final ConstantOrLerp[] mode;

    private final float[] r0, g0, b0, a0;
    private final float[] r1, g1, b1, a1;

    private final Easing[] easing;

    public ColorStorage(int capacity) {
        this.mode = new ConstantOrLerp[capacity];

        this.r0 = new float[capacity];
        this.g0 = new float[capacity];
        this.b0 = new float[capacity];
        this.a0 = new float[capacity];

        this.r1 = new float[capacity];
        this.g1 = new float[capacity];
        this.b1 = new float[capacity];
        this.a1 = new float[capacity];

        this.easing = new Easing[capacity];
    }

    @Override
    public void preRender(int liveCount, ParticleView particles, float partialTicks) {
        int[] age = particles.age();
        int[] lifetime = particles.lifetime();

        for (int i = 0; i < liveCount; i++) {

            switch (mode[i]) {
                case ConstantOrLerp.CONSTANT -> {
                    particles.r()[i] = r0[i];
                    particles.g()[i] = g0[i];
                    particles.b()[i] = b0[i];
                    particles.a()[i] = a0[i];
                }

                case ConstantOrLerp.LERP -> {
                    float t = lifetime[i] <= 0 ? 1.0f : (float) age[i] / (float) lifetime[i];
                    t = easing[i].ease(t);

                    particles.r()[i] = lerp(r0[i], r1[i], t);
                    particles.g()[i] = lerp(g0[i], g1[i], t);
                    particles.b()[i] = lerp(b0[i], b1[i], t);
                    particles.a()[i] = lerp(a0[i], a1[i], t);
                }

                default -> throw new IllegalStateException("Unknown color mode " + mode[i]);
            }
        }
    }

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    @Override
    public void onSpawn(int particleIndex, ColorConfig config, ParticleSpawnContext spawnContext, ParticleView particles) {
        mode[particleIndex] = config.mode;

        switch (config.mode) {
            case ConstantOrLerp.CONSTANT -> {
                r0[particleIndex] = config.r0;
                g0[particleIndex] = config.g0;
                b0[particleIndex] = config.b0;
                a0[particleIndex] = config.a0;

                particles.r()[particleIndex] = config.r0;
                particles.g()[particleIndex] = config.g0;
                particles.b()[particleIndex] = config.b0;
                particles.a()[particleIndex] = config.a0;
            }

            case ConstantOrLerp.LERP -> {
                r0[particleIndex] = config.r0;
                g0[particleIndex] = config.g0;
                b0[particleIndex] = config.b0;
                a0[particleIndex] = config.a0;

                r1[particleIndex] = config.r1;
                g1[particleIndex] = config.g1;
                b1[particleIndex] = config.b1;
                a1[particleIndex] = config.a1;

                easing[particleIndex] = config.easing;

                particles.r()[particleIndex] = config.r0;
                particles.g()[particleIndex] = config.g0;
                particles.b()[particleIndex] = config.b0;
                particles.a()[particleIndex] = config.a0;
            }

            default -> throw new IllegalStateException("Unknown color mode: " + config.mode);
        }
    }

    @Override
    public void onSwapRemove(int deadIndex, int movedIndex, ParticleView particles) {
        mode[deadIndex] = mode[movedIndex];

        r0[deadIndex] = r0[movedIndex];
        g0[deadIndex] = g0[movedIndex];
        b0[deadIndex] = b0[movedIndex];
        a0[deadIndex] = a0[movedIndex];

        r1[deadIndex] = r1[movedIndex];
        g1[deadIndex] = g1[movedIndex];
        b1[deadIndex] = b1[movedIndex];
        a1[deadIndex] = a1[movedIndex];

        easing[deadIndex] = easing[movedIndex];
    }
}
