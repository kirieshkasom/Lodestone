package team.lodestar.lodestone.modules.rendering.particle.pooled.storage;

import team.lodestar.lodestone.modules.rendering.particle.pooled.runtime.ParticleSpawnContext;
import team.lodestar.lodestone.modules.rendering.particle.pooled.runtime.ParticleView;

public interface ParticleComponentStorage<T> {
    void onSpawn(int particleIndex, T config, ParticleSpawnContext spawnContext, ParticleView particles);

    void onSwapRemove(int deadIndex, int movedIndex, ParticleView particles);
}