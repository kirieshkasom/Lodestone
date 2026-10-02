package team.lodestar.lodestone.modules.rendering.particle.pooled.visual.types.mesh;

import team.lodestar.lodestone.modules.rendering.particle.pooled.pool.ParticlePool;
import team.lodestar.lodestone.modules.rendering.particle.pooled.visual.*;

public class MeshVisualRuntime implements ParticleVisualRuntime {
    private final MeshVisualConfig config;

    public MeshVisualRuntime(MeshVisualConfig config) {
        this.config = config;
    }

    @Override
    public void collect(ParticleVisualCollectContext context, ParticlePool pool, int liveCount) {
        if (liveCount <= 0 || config.renderType == null || config.vertexBuffer == null) {
            return;
        }

        var drawData = new MeshVisualDrawData(context.particles(), liveCount, context.targetVisualId(), config.offsetX, config.offsetY, config.offsetZ);
        var submission = new ParticleVisualSubmission(config.renderType, config.instanceFormat, config.vertexBuffer, drawData, MeshBatchRenderer.INSTANCE);
        context.collector().submit(submission);
    }
}