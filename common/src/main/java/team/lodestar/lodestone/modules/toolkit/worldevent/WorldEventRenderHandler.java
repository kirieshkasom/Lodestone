package team.lodestar.lodestone.modules.toolkit.worldevent;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;
import team.lodestar.lodestone.internal.worldevent.WorldEventRenderCallbackAccess;
import team.lodestar.lodestone.modules.rendering.*;
import team.lodestar.lodestone.registry.client.LodestoneWorldEventRenderers;
import team.lodestar.lodestone.internal.worldevent.WorldEventStorageAccess;

public class WorldEventRenderHandler {

    public static void renderWorldEvents(ClientLevel level, PoseStack poseStack, Camera camera, float partialTicks) {
        Vec3 cameraPos = camera.getPosition();
        poseStack.pushPose();
        poseStack.translate(-cameraPos.x(), -cameraPos.y(), -cameraPos.z());

        WorldEventAttachment worldData = WorldEventStorageAccess.get(level);
        for (WorldEventInstance instance : worldData.activeWorldEvents) {
            WorldEventRenderer<WorldEventInstance> renderer = LodestoneWorldEventRenderers.RENDERERS.get(instance.type);
            if (renderer != null) {
                if (renderer.canRender(instance)) {
                    MultiBufferSource.BufferSource target = LodestoneRenderingSystem.DEFERRED_RENDER.getTarget();
                    WorldEventRenderCallbackAccess.render(instance, renderer, poseStack, target, partialTicks);
                    renderer.render(instance, poseStack, target, partialTicks);
                }
            }
        }
        poseStack.popPose();
    }
}
