package team.lodestar.lodestone.modules.rendering.particle.pooled.visual.types.mesh;

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
import team.lodestar.lodestone.modules.rendering.particle.pooled.visual.instance.InstanceElement;
import team.lodestar.lodestone.modules.rendering.particle.pooled.visual.instance.InstanceFormat;
import team.lodestar.lodestone.systems.rendering.IVertexBuffer;

import java.nio.FloatBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL33.*;

public class MeshBatchRenderer implements ParticleVisualBatchRenderer {
    public static final MeshBatchRenderer INSTANCE = new MeshBatchRenderer();

    private int instanceVbo = -1;

    @Override
    public void renderBatch(ParticleVisualBatchKey key, List<ParticleVisualSubmission> submissions, DeltaTracker partialTicks, Matrix4f viewMat, Matrix4f projMat) {
        RenderType renderType = key.renderType();
        VertexBuffer meshBuffer = key.vertexBuffer();
        InstanceFormat instanceFormat = key.instanceFormat();

        if (meshBuffer == null || instanceFormat == null) return;

        int totalInstances = 0;
        for (ParticleVisualSubmission submission : submissions) {
            MeshVisualDrawData data = (MeshVisualDrawData) submission.drawData();
            totalInstances += data.liveCount();
        }

        if (totalInstances == 0) return;

        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        float pt = partialTicks.getGameTimeDeltaPartialTick(false);
        int strideFloats = instanceFormat.totalFloats();

        FloatBuffer instanceData = MemoryUtil.memAllocFloat(totalInstances * strideFloats);

        try {
            int currentInstanceOffset = 0;

            for (ParticleVisualSubmission submission : submissions) {
                MeshVisualDrawData data = (MeshVisualDrawData) submission.drawData();
                int targetId = data.targetVisualId();
                int liveCount = data.liveCount();

                synchronized (data.particles()) {
                    int elementOffset = 0;
                    int particlesWritten = 0;

                    for (InstanceElement element : instanceFormat.elements()) {
                        particlesWritten = element.writer().write(
                                data.particles(), liveCount, targetId, pt, camera,
                                instanceData, currentInstanceOffset, elementOffset, strideFloats
                        );
                        elementOffset += element.floatCount();
                    }

                    currentInstanceOffset += particlesWritten;
                }
            }

            instanceData.position(0);
            instanceData.limit(totalInstances * strideFloats);

            if (instanceVbo == -1) instanceVbo = glGenBuffers();

            glBindBuffer(GL_ARRAY_BUFFER, instanceVbo);
            glBufferData(GL_ARRAY_BUFFER, instanceData, GL_DYNAMIC_DRAW);

            renderType.setupRenderState();
            meshBuffer.bind();
            ShaderInstance shader = RenderSystem.getShader();

            int baseAttributes = meshBuffer.getFormat().getElements().size();
            int currentAttribute = baseAttributes;
            int offset = 0;

            for (InstanceElement element : instanceFormat.elements()) {
                glEnableVertexAttribArray(currentAttribute);
                glVertexAttribPointer(currentAttribute, element.floatCount(), GL_FLOAT, false, instanceFormat.strideBytes(), offset);
                glVertexAttribDivisor(currentAttribute, 1);

                offset += element.floatCount() * Float.BYTES;
                currentAttribute++;
            }

            IVertexBuffer.cast(meshBuffer).drawWithShaderInstanced(viewMat, projMat, shader, totalInstances);

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