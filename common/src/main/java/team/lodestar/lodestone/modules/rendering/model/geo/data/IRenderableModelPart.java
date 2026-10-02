package team.lodestar.lodestone.modules.rendering.model.geo.data;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;

public interface IRenderableModelPart<T> extends ICloneable<T> {

    void render(PoseStack poseStack, VertexConsumer vertexConsumer, VertexFormat vertexFormat, VertexFormat.Mode mode);

    default void render(PoseStack poseStack, VertexConsumer vertexConsumer, RenderType renderType) {
        this.render(poseStack, vertexConsumer, renderType.format(), renderType.mode());
    }
}
