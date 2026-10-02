package team.lodestar.lodestone.modules.rendering.particle.pooled.visual.types.billboard;

import com.mojang.blaze3d.vertex.*;
import team.lodestar.lodestone.modules.rendering.particle.pooled.pool.ParticlePool;
import team.lodestar.lodestone.modules.rendering.particle.pooled.visual.*;
import team.lodestar.lodestone.systems.rendering.LodestoneRenderSystem;

public class BillboardVisualRuntime implements ParticleVisualRuntime {
    private final BillboardVisualConfig config;
    private static VertexBuffer vertexBuffer;

    public BillboardVisualRuntime(BillboardVisualConfig config) {
        this.config = config;
    }

    @Override
    public void collect(ParticleVisualCollectContext context, ParticlePool pool, int liveCount) {
        if (liveCount <= 0 || config.renderType == null) {
            return;
        }
        if (vertexBuffer == null) {
            LodestoneRenderSystem.wrap(BillboardVisualRuntime::initVBO);
        }
        var drawData = new BillboardVisualDrawData(context.particles(), liveCount, context.targetVisualId());
        var submission = new ParticleVisualSubmission(config.renderType, config.instanceFormat, vertexBuffer, drawData, BillboardBatchRenderer.INSTANCE);
        context.collector().submit(submission);
    }

    private static void initVBO() {
        vertexBuffer = new VertexBuffer(VertexBuffer.Usage.DYNAMIC);
        BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        builder.addVertex(-0.5f, -0.5f, 0.0f).setUv(1.0f, 1.0f);
        builder.addVertex( 0.5f, -0.5f, 0.0f).setUv(0.0f, 1.0f);
        builder.addVertex( 0.5f,  0.5f, 0.0f).setUv(0.0f, 0.0f);
        builder.addVertex(-0.5f,  0.5f, 0.0f).setUv(1.0f, 0.0f);

        MeshData meshData = builder.buildOrThrow();
        vertexBuffer.bind();
        vertexBuffer.upload(meshData);
        VertexBuffer.unbind();
    }
}