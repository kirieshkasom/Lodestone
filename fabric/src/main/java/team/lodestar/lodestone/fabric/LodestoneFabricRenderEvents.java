package team.lodestar.lodestone.fabric;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import org.joml.Matrix4f;
import team.lodestar.lodestone.modules.rendering.LodestoneRenderingSystem;
import team.lodestar.lodestone.modules.rendering.RenderPhase;

public final class LodestoneFabricRenderEvents {
    private LodestoneFabricRenderEvents() {
    }

    public static void install() {
        WorldRenderEvents.AFTER_ENTITIES.register(context -> renderPhase(RenderPhase.AFTER_ENTITIES, context));
        WorldRenderEvents.END.register(context -> renderPhase(RenderPhase.AFTER_LEVEL, context));
    }

    public static void renderPhase(RenderPhase phase, WorldRenderContext context) {
        PoseStack poseStack = context.matrixStack();
        if (poseStack == null) {
            poseStack = new PoseStack();
        }
        LodestoneRenderingSystem.renderPhase(phase, poseStack, context.camera(), context.tickCounter().getGameTimeDeltaPartialTick(false), new Matrix4f(context.positionMatrix()), new Matrix4f(context.projectionMatrix()));
    }

    public static void renderPhase(RenderPhase phase) {
        Minecraft minecraft = Minecraft.getInstance();
        PoseStack poseStack = new PoseStack();
        LodestoneRenderingSystem.renderPhase(phase, poseStack, minecraft.gameRenderer.getMainCamera(), minecraft.getTimer().getGameTimeDeltaPartialTick(false), new Matrix4f(RenderSystem.getModelViewMatrix()), new Matrix4f(RenderSystem.getProjectionMatrix()));
    }
}
