package team.lodestar.lodestone.modules.rendering.particle.pooled.pool;

import team.lodestar.lodestone.modules.rendering.particle.pooled.builder.ParticleSpec;
import team.lodestar.lodestone.modules.rendering.particle.pooled.runtime.ParticleSpawnContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class DelayedParticles {

    protected final ParticleSpec spec;
    protected final List<ParticleSpawnContext> particles = new ArrayList<>();

    public DelayedParticles(ParticleSpec spec) {
        this.spec = spec;
    }

    public void tryAdd(ParticleSpec spec, ParticleSpawnContext context) {
        if (this.spec.equals(spec)) {
            particles.add(context);
        }
    }
}
