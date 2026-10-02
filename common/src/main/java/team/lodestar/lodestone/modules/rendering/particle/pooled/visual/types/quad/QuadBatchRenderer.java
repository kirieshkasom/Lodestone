package team.lodestar.lodestone.modules.rendering.particle.pooled.visual.types.quad;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexBuffer;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;
import team.lodestar.lodestone.modules.rendering.particle.pooled.visual.ParticleVisualBatchKey;
import team.lodestar.lodestone.modules.rendering.particle.pooled.visual.ParticleVisualBatchRenderer;
import team.lodestar.lodestone.modules.rendering.particle.pooled.visual.ParticleVisualSubmission;
import team.lodestar.lodestone.modules.rendering.particle.pooled.visual.instance.InstanceAttribute;
import team.lodestar.lodestone.modules.rendering.particle.pooled.visual.instance.InstanceElement;
import team.lodestar.lodestone.modules.rendering.particle.pooled.visual.instance.InstanceFormat;
import team.lodestar.lodestone.modules.rendering.particle.pooled.visual.instance.InstanceWriter;
import team.lodestar.lodestone.systems.rendering.IVertexBuffer;

import java.nio.FloatBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL33.*;

public class QuadBatchRenderer implements ParticleVisualBatchRenderer {
    public static final QuadBatchRenderer INSTANCE = new QuadBatchRenderer();

    private int instanceVbo = -1;

    @Override
    public void renderBatch(ParticleVisualBatchKey key, List<ParticleVisualSubmission> submissions, DeltaTracker partialTicks, Matrix4f viewMat, Matrix4f projMat) {
        RenderType renderType = key.renderType();
        VertexBuffer quadBuffer = key.vertexBuffer();
        InstanceFormat instanceFormat = key.instanceFormat();

        if (quadBuffer == null || instanceFormat == null) return;

        int estimatedInstances = 0;
        for (ParticleVisualSubmission submission : submissions) {
            QuadVisualDrawData data = (QuadVisualDrawData) submission.drawData();
            estimatedInstances += data.liveCount();
        }

        if (estimatedInstances == 0) return;

        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        float pt = partialTicks.getGameTimeDeltaPartialTick(false);
        int strideFloats = instanceFormat.totalFloats();

        FloatBuffer instanceData = MemoryUtil.memAllocFloat(estimatedInstances * strideFloats);

        try {
            int currentInstanceOffset = 0;

            for (ParticleVisualSubmission submission : submissions) {
                QuadVisualDrawData data = (QuadVisualDrawData) submission.drawData();
                int targetId = data.targetVisualId();
                int liveCount = data.liveCount();

                synchronized (data.particles()) {
                    int elementOffset = 0;
                    int particlesWritten = 0;

                    for (InstanceElement element : instanceFormat.elements()) {
                        particlesWritten = element.writer().write(data.particles(), liveCount, targetId, pt, camera, instanceData, currentInstanceOffset, elementOffset, strideFloats);
                        elementOffset += element.floatCount();
                    }

                    currentInstanceOffset += particlesWritten;
                }
            }

            int actualInstances = currentInstanceOffset;
            if (actualInstances == 0) return;

            instanceData.position(0);
            instanceData.limit(actualInstances * strideFloats);

            if (instanceVbo == -1) instanceVbo = glGenBuffers();

            glBindBuffer(GL_ARRAY_BUFFER, instanceVbo);
            glBufferData(GL_ARRAY_BUFFER, instanceData, GL_DYNAMIC_DRAW);

            renderType.setupRenderState();
            quadBuffer.bind();
            ShaderInstance shader = RenderSystem.getShader();

            glBindBuffer(GL_ARRAY_BUFFER, instanceVbo);

            int baseAttributes = quadBuffer.getFormat().getElements().size();
            int currentAttribute = baseAttributes;
            int elementOffsetFloats = 0;

            for (InstanceElement element : instanceFormat.elements()) {
                InstanceWriter writer = element.writer();

                for (InstanceAttribute attribute : writer.attributes()) {
                    glEnableVertexAttribArray(currentAttribute);
                    glVertexAttribPointer(currentAttribute, attribute.componentCount(), GL_FLOAT, false, instanceFormat.strideBytes(), (long) (elementOffsetFloats + attribute.offsetFloats()) * Float.BYTES);
                    glVertexAttribDivisor(currentAttribute, attribute.divisor());
                    currentAttribute++;
                }

                elementOffsetFloats += writer.floatCount();
            }

            IVertexBuffer.cast(quadBuffer).drawWithShaderInstanced(viewMat, projMat, shader, actualInstances);

            for (int i = baseAttributes; i < currentAttribute; i++) {
                glVertexAttribDivisor(i, 0);
                glDisableVertexAttribArray(i);
            }

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            VertexBuffer.unbind();
            renderType.clearRenderState();
        } finally {
            MemoryUtil.memFree(instanceData);
        }
    }
}