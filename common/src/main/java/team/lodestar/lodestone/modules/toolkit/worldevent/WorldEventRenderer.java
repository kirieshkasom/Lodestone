package team.lodestar.lodestone.modules.toolkit.worldevent;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;

public abstract class WorldEventRenderer<T extends WorldEventInstance> {

    public abstract boolean canRender(T instance);

    public abstract void render(T instance, PoseStack poseStack, MultiBufferSource bufferSource, float partialTicks);
}
