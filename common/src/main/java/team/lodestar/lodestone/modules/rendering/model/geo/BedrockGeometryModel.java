package team.lodestar.lodestone.modules.rendering.model.geo;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import team.lodestar.lodestone.modules.rendering.model.IRenderableModel;
import team.lodestar.lodestone.modules.rendering.model.geo.data.GeoBone;

public class BedrockGeometryModel implements IRenderableModel {
    private final ResourceLocation location;
    protected VertexBuffer modelBuffer;
    protected GeoBone root;

    public BedrockGeometryModel(ResourceLocation location) {
        this.location = location;
    }

    @Override
    public ResourceLocation getModelLocation() {
        return this.location;
    }

    @Override
    public void loadModel() {
        BedrockGeometryParser parser = new BedrockGeometryParser();
        parser.startParse(this);
    }

    @Override
    public void render(PoseStack poseStack, VertexConsumer vertexConsumer, RenderType renderType) {
        this.root.render(poseStack, vertexConsumer, renderType);
    }

    @Override
    public VertexBuffer getModelBuffer() {
        return this.modelBuffer;
    }

    @Override
    public void setModelBuffer(VertexBuffer buffer) {
        this.modelBuffer = buffer;
    }

    @Override
    public MeshData createMesh(PoseStack poseStack, VertexFormat vertexFormat, VertexFormat.Mode mode) {
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(mode, vertexFormat);
        this.root.render(poseStack, bufferBuilder, vertexFormat, mode);
        return bufferBuilder.buildOrThrow();
    }

    public GeoBone getRootBone() {
        return this.root;
    }
}
