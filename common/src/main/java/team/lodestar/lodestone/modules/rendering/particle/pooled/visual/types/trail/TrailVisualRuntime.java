package team.lodestar.lodestone.modules.rendering.particle.pooled.visual.types.trail;

import team.lodestar.lodestone.modules.rendering.particle.pooled.pool.ParticlePool;
import team.lodestar.lodestone.modules.rendering.particle.pooled.visual.*;

import java.util.IdentityHashMap;
import java.util.Map;

public class TrailVisualRuntime implements ParticleVisualRuntime {
    private final TrailVisualConfig config;
    private final Map<ParticlePool, TrailVisualStorage> storages = new IdentityHashMap<>();

    public TrailVisualRuntime(TrailVisualConfig config) {
        this.config = config;
    }

    private TrailVisualStorage getStorage(ParticlePool pool) {
        return storages.computeIfAbsent(pool, p -> {
            TrailVisualStorage storage = new TrailVisualStorage(p.capacity(), config.maxPoints);
            p.addSwapRemoveListener(storage::onSwapRemove);
            return storage;
        });
    }

    @Override
    public void collect(ParticleVisualCollectContext context, ParticlePool pool, int liveCount) {
        if (liveCount <= 0 || config.renderType == null) return;

        TrailVisualStorage storage = getStorage(pool);
        storage.capture(context.particles(), liveCount, context.targetVisualId());

        var drawData = new TrailVisualDrawData(context.particles(), storage, liveCount, context.targetVisualId(), config.width);
        var submission = new ParticleVisualSubmission(config.renderType, null, null, drawData, TrailBatchRenderer.INSTANCE);
        context.collector().submit(submission);
    }
}