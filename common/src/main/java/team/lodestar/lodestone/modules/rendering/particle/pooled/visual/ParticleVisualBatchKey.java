package team.lodestar.lodestone.modules.rendering.particle.pooled.visual;

import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.client.renderer.RenderType;
import team.lodestar.lodestone.modules.rendering.particle.pooled.visual.instance.InstanceFormat;

public class ParticleVisualBatchKey {
    private final RenderType renderType;
    private final InstanceFormat instanceFormat;
    private final VertexBuffer vertexBuffer;
    private final ParticleVisualBatchRenderer batchRenderer;

    public ParticleVisualBatchKey(RenderType renderType, InstanceFormat instanceFormat, VertexBuffer vertexBuffer, ParticleVisualBatchRenderer batchRenderer) {
        this.renderType = renderType;
        this.instanceFormat = instanceFormat;
        this.vertexBuffer = vertexBuffer;
        this.batchRenderer = batchRenderer;
    }

    public RenderType renderType() {
        return renderType;
    }

    public VertexBuffer vertexBuffer() {
        return vertexBuffer;
    }

    public InstanceFormat instanceFormat() {
        return instanceFormat;
    }

    public static ParticleVisualBatchKey fromSubmission(ParticleVisualSubmission submission) {
        return new ParticleVisualBatchKey(submission.renderType(), submission.instanceFormat(), submission.vertexBuffer(), submission.renderer());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParticleVisualBatchKey that)) return false;
        return renderType == that.renderType
                && instanceFormat == that.instanceFormat
                && vertexBuffer == that.vertexBuffer
                && batchRenderer == that.batchRenderer;
    }

    @Override
    public int hashCode() {
        int result = System.identityHashCode(renderType);
        result = 31 * result + System.identityHashCode(instanceFormat);
        result = 31 * result + System.identityHashCode(vertexBuffer);
        result = 31 * result + System.identityHashCode(batchRenderer);
        return result;
    }
}