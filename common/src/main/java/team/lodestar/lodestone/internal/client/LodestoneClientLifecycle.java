package team.lodestar.lodestone.internal.client;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import team.lodestar.lodestone.handlers.ScreenshakeHandler;
import team.lodestar.lodestone.handlers.screenparticle.ParticleEmitterHandler;
import team.lodestar.lodestone.handlers.screenparticle.ScreenParticleHandler;
import team.lodestar.lodestone.internal.LodestoneCommon;
import team.lodestar.lodestone.registry.client.LodestoneModels;
import team.lodestar.lodestone.systems.rendering.LodestoneRenderSystem;
import team.lodestar.lodestone.systems.rendering.renderpass.RenderPassHandler;

import java.util.function.Consumer;

public final class LodestoneClientLifecycle {
    private LodestoneClientLifecycle() {
    }

    public static void initialize() {
        ParticleEmitterHandler.registerParticleEmitters();
    }

    public static void clientTick(Minecraft minecraft, Consumer<ClientLevel> levelTick) {
        ClientLevel level = minecraft.level;
        if (level != null) {
            if (minecraft.isPaused()) {
                return;
            }
            Camera camera = minecraft.gameRenderer.getMainCamera();
            levelTick.accept(level);
            ScreenshakeHandler.clientTick(level, camera);
            ScreenParticleHandler.tickParticles();
        }
    }

    public static void shutdown() {
        LodestoneRenderSystem.wrap(() -> {
            LodestoneModels.cleanup();
            LodestoneRenderSystem.destroyBufferObjects();
            RenderPassHandler.close();
            LodestoneCommon.LOGGER.info("Shutting down Lodestone");
        });
    }
}
