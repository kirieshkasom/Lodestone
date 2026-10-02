package team.lodestar.lodestone.modules.rendering.particle.pooled.pool;

import team.lodestar.lodestone.modules.rendering.particle.pooled.builder.ParticleSpec;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ParticlePoolGroup {
    private final ParticlePoolKey key;
    private final List<ParticlePool> pools = new CopyOnWriteArrayList<>();
    private final int poolCapacity;

    public ParticlePoolGroup(ParticlePoolKey key, int poolCapacity) {
        this.key = key;
        this.poolCapacity = poolCapacity;
    }

    public ParticlePoolKey key() {
        return key;
    }

    public List<ParticlePool> pools() {
        return Collections.unmodifiableList(pools);
    }

    public ParticlePool claimPool(ParticleSpec spec) {
        for (ParticlePool pool : pools) {
            if (!pool.isFull()) {
                return pool;
            }
        }
        ParticlePool pool = new ParticlePool(poolCapacity, spec);
        pools.add(pool);
        return pool;
    }

    public void tick(float dt) {
        for (ParticlePool pool : pools) {
            if (pool.count() > 0) {
                pool.tick(dt);
            }
        }
    }

    public boolean isEmpty() {
        for (ParticlePool pool : pools) {
            if (pool.count() > 0) {
                return false;
            }
        }
        return true;
    }
}