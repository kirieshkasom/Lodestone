package team.lodestar.lodestone.events;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import team.lodestar.lodestone.modules.rendering.LodestoneRenderingSystem;
import team.lodestar.lodestone.modules.rendering.RenderPhase;
import team.lodestar.lodestone.modules.rendering.handlers.ParticleHandler;

@EventBusSubscriber(value = Dist.CLIENT)
public final class LodestoneNeoForgeRenderEvents {
    private LodestoneNeoForgeRenderEvents() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void renderLevelStage(RenderLevelStageEvent event) {
        RenderPhase phase = renderPhase(event.getStage());
        if (phase != null) {
            LodestoneRenderingSystem.renderPhase(phase, event.getPoseStack(), event.getCamera(), event.getPartialTick().getGameTimeDeltaPartialTick(false), event.getModelViewMatrix(), event.getProjectionMatrix());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void renderFog(ViewportEvent.RenderFog event) {
        LodestoneRenderingSystem.cacheFogData(event.getNearPlaneDistance(), event.getFarPlaneDistance(), event.getFogShape());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void fogColors(ViewportEvent.ComputeFogColor event) {
        LodestoneRenderingSystem.cacheFogColors(event.getRed(), event.getGreen(), event.getBlue());
    }

    @SubscribeEvent
    public static void clientLevelTick(LevelTickEvent.Pre event) {
        if (event.getLevel().isClientSide()) {
            ParticleHandler.tickClientParticles();
        }
    }

    @SubscribeEvent
    public static void clientLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel().isClientSide()) {
            ParticleHandler.clearClientParticles();
        }
    }

    private static RenderPhase renderPhase(RenderLevelStageEvent.Stage stage) {
        if (stage == RenderLevelStageEvent.Stage.AFTER_SKY) {
            return RenderPhase.AFTER_SKY;
        }
        if (stage == RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS) {
            return RenderPhase.AFTER_SOLID_BLOCKS;
        }
        if (stage == RenderLevelStageEvent.Stage.AFTER_CUTOUT_MIPPED_BLOCKS_BLOCKS) {
            return RenderPhase.AFTER_CUTOUT_MIPPED_BLOCKS;
        }
        if (stage == RenderLevelStageEvent.Stage.AFTER_CUTOUT_BLOCKS) {
            return RenderPhase.AFTER_CUTOUT_BLOCKS;
        }
        if (stage == RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            return RenderPhase.AFTER_ENTITIES;
        }
        if (stage == RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
            return RenderPhase.AFTER_BLOCK_ENTITIES;
        }
        if (stage == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return RenderPhase.AFTER_TRANSLUCENT_BLOCKS;
        }
        if (stage == RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS) {
            return RenderPhase.AFTER_TRIPWIRE_BLOCKS;
        }
        if (stage == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return RenderPhase.AFTER_PARTICLES;
        }
        if (stage == RenderLevelStageEvent.Stage.AFTER_WEATHER) {
            return RenderPhase.AFTER_WEATHER;
        }
        if (stage == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
            return RenderPhase.AFTER_LEVEL;
        }
        return null;
    }
}
