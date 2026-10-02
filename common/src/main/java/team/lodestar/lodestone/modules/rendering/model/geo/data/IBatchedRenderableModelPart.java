package team.lodestar.lodestone.modules.rendering.model.geo.data;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;

public interface IBatchedRenderableModelPart<T> extends IRenderableModelPart<T> {
    void renderBatched(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource);
}