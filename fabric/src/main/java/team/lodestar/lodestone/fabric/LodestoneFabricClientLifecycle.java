package team.lodestar.lodestone.fabric;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import team.lodestar.lodestone.internal.LodestoneCommon;
import team.lodestar.lodestone.internal.client.LodestoneClientLifecycle;
import team.lodestar.lodestone.modules.toolkit.worldevent.WorldEventHandler;
import team.lodestar.lodestone.modules.rendering.handlers.ModelHandler;
import team.lodestar.lodestone.registry.client.LodestonePostProcessEffects;
import team.lodestar.lodestone.registry.common.particle.LodestoneScreenParticleTypes;

public final class LodestoneFabricClientLifecycle {
    private static volatile boolean refreshScreenParticleFactories;

    private LodestoneFabricClientLifecycle() {
    }

    public static void install() {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return LodestoneCommon.lodestonePath("screen_particle_sprite_refresh");
            }

            @Override
            public void onResourceManagerReload(ResourceManager resourceManager) {
                refreshScreenParticleFactories = true;
            }
        });
        ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register((minecraft, level) -> LodestoneFabric.WORLD_EVENT_STORAGE.clearClient());
        ClientPlayConnectionEvents.DISCONNECT.register((handler, minecraft) -> LodestoneFabric.WORLD_EVENT_STORAGE.clearClient());
        ClientLifecycleEvents.CLIENT_STARTED.register(minecraft -> {
            LodestoneClientLifecycle.initialize();
            ModelHandler.clientInit();
            LodestonePostProcessEffects.setupPostProcessEffects();
            LodestoneScreenParticleTypes.registerParticleFactory();
        });
        ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
            LodestoneClientLifecycle.clientTick(minecraft, WorldEventHandler::tick);
            if (refreshScreenParticleFactories) {
                refreshScreenParticleFactories = false;
                LodestoneScreenParticleTypes.registerParticleFactory();
            }
        });
        ClientLifecycleEvents.CLIENT_STOPPING.register(minecraft -> LodestoneClientLifecycle.shutdown());
    }
}
