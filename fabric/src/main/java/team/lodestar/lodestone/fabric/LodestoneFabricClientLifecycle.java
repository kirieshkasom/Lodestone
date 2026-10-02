package team.lodestar.lodestone.fabric;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import team.lodestar.lodestone.internal.client.LodestoneClientLifecycle;

public final class LodestoneFabricClientLifecycle {
    private LodestoneFabricClientLifecycle() {
    }

    public static void install() {
        ClientLifecycleEvents.CLIENT_STARTED.register(minecraft -> LodestoneClientLifecycle.initialize());
        ClientTickEvents.END_CLIENT_TICK.register(minecraft -> LodestoneClientLifecycle.clientTick(minecraft, level -> {
        }));
        ClientLifecycleEvents.CLIENT_STOPPING.register(minecraft -> LodestoneClientLifecycle.shutdown());
    }
}
