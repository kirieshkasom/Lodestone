package team.lodestar.lodestone.modules.rendering.model;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;
import team.lodestar.lodestone.systems.rendering.IVertexBuffer;

public interface IRenderableModel {

    /**
     * Gets the model's ResourceLocation
     * @return The model's ResourceLocation
     */
    ResourceLocation getModelLocation();

    /**
     * Loads the model data from its source
     */
    void loadModel();

    /**
     * Renders the model using the provided PoseStack and VertexConsumer
     */
    void render(PoseStack poseStack, VertexConsumer vertexConsumer, RenderType renderType);

    /**
     * Gets the model's VertexBuffer
     * @return The model's VertexBuffer
     */
    VertexBuffer getModelBuffer();

    /**
     * Sets the model's VertexBuffer
     * @param buffer The VertexBuffer to set
     */
    void setModelBuffer(VertexBuffer buffer);

    /**
     * Creates a MeshData object for the model typically used in instanced rendering.
     * @param poseStack The PoseStack to use
     * @param vertexFormat The VertexFormat to use
     * @param mode The VertexFormat.Mode to use
     * @return The created MeshData
     */
    MeshData createMesh(PoseStack poseStack, VertexFormat vertexFormat, VertexFormat.Mode mode);

    /**
     * Renders the model using the provided PoseStack and RenderType
     * @param poseStack poseStack
     * @param renderType renderType
     * @param bufferSource bufferSource
     */
    default void render(PoseStack poseStack, RenderType renderType, MultiBufferSource.BufferSource bufferSource) {
        VertexConsumer vertexConsumer = bufferSource.getBuffer(renderType);
        this.render(poseStack, vertexConsumer, renderType);
    }

    /**
     * Renders the model using instanced rendering using a generated VertexBuffer.
     * @param poseStack The PoseStack to use
     * @param frustrumMatrix The view matrix
     * @param projectionMatrix The projection matrix
     * @param renderType The RenderType to use
     * @param instances Number of instances to render
     */
    default void renderInstanced(PoseStack poseStack, Matrix4f frustrumMatrix, Matrix4f projectionMatrix, RenderType renderType, int instances) {
        this.createModelBuffer(poseStack, renderType);
        this.getModelBuffer().bind();
        renderType.setupRenderState();
        IVertexBuffer.cast(this.getModelBuffer()).drawWithShaderInstanced(frustrumMatrix, projectionMatrix, RenderSystem.getShader(), instances);
        renderType.clearRenderState();
        VertexBuffer.unbind();
    }

    /**
     * Creates and uploads the model's VertexBuffer if it doesn't already exist.
     * @param poseStack
     * @param renderType
     */
    @ApiStatus.Internal
    default VertexBuffer createModelBuffer(PoseStack poseStack, RenderType renderType) {
        if (this.getModelBuffer() == null) {
            this.setModelBuffer(new VertexBuffer(VertexBuffer.Usage.DYNAMIC));
        }
        this.getModelBuffer().bind();
        this.getModelBuffer().upload(this.createMesh(poseStack, renderType));
        VertexBuffer.unbind();
        return getModelBuffer();
    }

    @ApiStatus.Internal
    default MeshData createMesh(PoseStack poseStack, RenderType renderType) {
        return this.createMesh(poseStack, renderType.format(), renderType.mode());
    }

    /**
     * Cleans up the model's VertexBuffer
     */
    @ApiStatus.Internal
    default void cleanup() {
        VertexBuffer buffer = this.getModelBuffer();
        if (buffer != null) {
            buffer.close();
            this.setModelBuffer(null);
        }
    }
}
