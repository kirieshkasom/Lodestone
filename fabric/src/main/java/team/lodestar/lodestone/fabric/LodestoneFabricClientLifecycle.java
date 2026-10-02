package team.lodestar.lodestone.fabric;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import team.lodestar.lodestone.internal.client.LodestoneClientLifecycle;
import team.lodestar.lodestone.modules.toolkit.worldevent.WorldEventHandler;

public final class LodestoneFabricClientLifecycle {
    private LodestoneFabricClientLifecycle() {
    }

    public static void install() {
        ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register((minecraft, level) -> LodestoneFabric.WORLD_EVENT_STORAGE.clearClient());
        ClientPlayConnectionEvents.DISCONNECT.register((handler, minecraft) -> LodestoneFabric.WORLD_EVENT_STORAGE.clearClient());
        ClientLifecycleEvents.CLIENT_STARTED.register(minecraft -> LodestoneClientLifecycle.initialize());
        ClientTickEvents.END_CLIENT_TICK.register(minecraft -> LodestoneClientLifecycle.clientTick(minecraft, WorldEventHandler::tick));
        ClientLifecycleEvents.CLIENT_STOPPING.register(minecraft -> LodestoneClientLifecycle.shutdown());
    }
}
