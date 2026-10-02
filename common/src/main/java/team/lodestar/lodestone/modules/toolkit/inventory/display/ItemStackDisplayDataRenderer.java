package team.lodestar.lodestone.modules.toolkit.inventory.display;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import team.lodestar.lodestone.modules.toolkit.inventory.LodestoneItemStackBlockHandler;

import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

public class ItemStackDisplayDataRenderer {

    public ItemStackDisplayDataRenderer() {

    }

    public void render(LodestoneItemStackBlockHandler handler, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, float partialTicks) {
        if (handler == null) {
            return;
        }
        var level = Minecraft.getInstance().level;
        var itemRenderer = Minecraft.getInstance().getItemRenderer();
        var parent = handler.getParent();
        var pos = parent.getBlockPos();

        var displayData = handler.getDisplayData();
        assert displayData != null;
        var stacks = handler.getStacks();

        for (int i = 0; i < stacks.size(); i++) {
            var optional = displayData.getEntry(i);
            if (optional.isEmpty()) {
                continue;
            }
            var entry = optional.get();
            var stack = entry.getStack();
            var scale = entry.getScale(partialTicks);
            var itemRotation = entry.getItemRotation(partialTicks);

            var position = displayData.getItemPosition(entry, partialTicks);
            var xt = position.x - pos.getX();
            var yt = position.y - pos.getY();
            var zt = position.z - pos.getZ();
            poseStack.pushPose();
            poseStack.translate(xt, yt, zt);
            poseStack.mulPose(Axis.YP.rotation(itemRotation));
            poseStack.scale(scale, scale, scale);
            itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, combinedLightIn, NO_OVERLAY, poseStack, bufferIn, level, 0);
            poseStack.popPose();
        }
    }
}
