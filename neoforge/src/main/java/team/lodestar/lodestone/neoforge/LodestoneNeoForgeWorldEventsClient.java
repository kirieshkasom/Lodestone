package team.lodestar.lodestone.neoforge;

import team.lodestar.lodestone.internal.worldevent.WorldEventRendererRegistration;
import team.lodestar.lodestone.internal.worldevent.WorldEventRenderCallbackAccess;
import team.lodestar.lodestone.registry.client.LodestoneWorldEventRenderers;
import team.lodestar.lodestone.internal.worldevent.WorldEventCommandContext;
import net.minecraft.client.Minecraft;
import team.lodestar.lodestone.internal.worldevent.NeoForgeWorldEventRenderCallback;

public final class LodestoneNeoForgeWorldEventsClient {
    private LodestoneNeoForgeWorldEventsClient() {
    }

    public static void install() {
        LodestoneNeoForgeParticleModels.install();
        team.lodestar.lodestone.modules.rendering.texture.StencilPlatform.installNativeEnabler(com.mojang.blaze3d.pipeline.RenderTarget::enableStencil);
        WorldEventCommandContext.clientLevelSupplier(() -> Minecraft.getInstance().level);
        WorldEventRendererRegistration.install((type, renderer) -> LodestoneWorldEventRenderers.registerRenderer(type, renderer == null ? null : renderer.get()));
        WorldEventRenderCallbackAccess.install(new NeoForgeWorldEventRenderCallback());
    }
}
