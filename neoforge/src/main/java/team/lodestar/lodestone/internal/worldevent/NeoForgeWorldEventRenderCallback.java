package team.lodestar.lodestone.internal.worldevent;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.neoforged.neoforge.common.NeoForge;
import team.lodestar.lodestone.events.types.worldevent.WorldEventRenderEvent;
import team.lodestar.lodestone.modules.toolkit.worldevent.WorldEventInstance;
import team.lodestar.lodestone.modules.toolkit.worldevent.WorldEventRenderer;

public final class NeoForgeWorldEventRenderCallback implements WorldEventRenderCallback {
    @Override
    public void render(WorldEventInstance instance, WorldEventRenderer<WorldEventInstance> renderer, PoseStack poseStack, MultiBufferSource multiBufferSource, float partialTicks) {
        NeoForge.EVENT_BUS.post(new WorldEventRenderEvent(instance, renderer, poseStack, multiBufferSource, partialTicks));
    }
}
