package team.lodestar.lodestone.fabric;

import team.lodestar.lodestone.internal.worldevent.WorldEventRendererRegistration;
import team.lodestar.lodestone.internal.worldevent.WorldEventRenderCallbackAccess;
import team.lodestar.lodestone.registry.client.LodestoneWorldEventRenderers;
import team.lodestar.lodestone.internal.worldevent.WorldEventCommandContext;
import net.minecraft.client.Minecraft;
import team.lodestar.lodestone.fabric.events.FabricWorldEventRenderEvents;

public final class LodestoneFabricWorldEventsClient {
    private LodestoneFabricWorldEventsClient() {
    }

    public static void install() {
        WorldEventCommandContext.clientLevelSupplier(() -> Minecraft.getInstance().level);
        WorldEventRendererRegistration.install((type, renderer) -> LodestoneWorldEventRenderers.registerRenderer(type, renderer == null ? null : renderer.get()));
        WorldEventRenderCallbackAccess.install((instance, renderer, poseStack, buffers, partialTicks) -> FabricWorldEventRenderEvents.EVENT.invoker().render(instance, renderer, poseStack, buffers, partialTicks));
    }
}
