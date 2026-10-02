package team.lodestar.lodestone.modules.rendering.particle.pooled.visual;

import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.client.renderer.RenderType;
import team.lodestar.lodestone.modules.rendering.particle.pooled.visual.instance.InstanceFormat;

public class ParticleVisualSubmission {
    private final RenderType renderType;
    private final InstanceFormat instanceFormat;
    private final VertexBuffer vertexBuffer;
    private final ParticleVisualDrawData drawData;
    private final ParticleVisualBatchRenderer renderer;

    public ParticleVisualSubmission(RenderType renderType, InstanceFormat instanceFormat, VertexBuffer vertexBuffer, ParticleVisualDrawData drawData, ParticleVisualBatchRenderer batchRenderer) {
        this.renderType = renderType;
        this.instanceFormat = instanceFormat;
        this.vertexBuffer = vertexBuffer;
        this.drawData = drawData;
        this.renderer = batchRenderer;
    }

    public RenderType renderType() {
        return renderType;
    }

    public InstanceFormat instanceFormat() {
        return instanceFormat;
    }

    public VertexBuffer vertexBuffer() {
        return vertexBuffer;
    }

    public ParticleVisualDrawData drawData() {
        return drawData;
    }

    public ParticleVisualBatchRenderer renderer() {
        return renderer;
    }
}