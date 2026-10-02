package team.lodestar.lodestone.internal.network;

import team.lodestar.lodestone.modules.toolkit.screenshake.ScreenshakePayload;
import team.lodestar.lodestone.modules.toolkit.worldevent.SyncWorldEventPayload;
import team.lodestar.lodestone.modules.toolkit.worldevent.UpdateWorldEventPayload;
import team.lodestar.lodestone.systems.network.particle.NetworkedParticleEffectPayload;

public final class LodestonePayloads {
    private LodestonePayloads() {
    }

    public static void register(ClientPayloadRegistrar registrar) {
        registrar.register("sync_world_event", SyncWorldEventPayload.class, SyncWorldEventPayload::new);
        registrar.register("update_world_event", UpdateWorldEventPayload.class, UpdateWorldEventPayload::new);
        registrar.register("screenshake", ScreenshakePayload.class, ScreenshakePayload::new);
        registrar.register("particle_effect", NetworkedParticleEffectPayload.class, NetworkedParticleEffectPayload::new);
    }
}
