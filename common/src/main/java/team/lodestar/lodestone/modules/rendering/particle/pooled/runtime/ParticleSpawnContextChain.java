package team.lodestar.lodestone.modules.rendering.particle.pooled.runtime;

import team.lodestar.lodestone.modules.rendering.particle.pooled.runtime.profile.ParticleSpawnProfile;

public class ParticleSpawnContextChain {
    private final ParticleSpawnProfile[] profiles;

    public ParticleSpawnContextChain(ParticleSpawnProfile... profiles) {
        this.profiles = profiles;
    }

    public ParticleSpawnContext apply(ParticleSpawnContext ctx, int index, int count) {
        for (ParticleSpawnProfile profile : profiles) {
            profile.apply(ctx, index, count);
        }
        return ctx;
    }

    public ParticleSpawnContext apply(ParticleSpawnContext ctx) {
        return apply(ctx, 0, 1);
    }
}
