package team.lodestar.lodestone.internal.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

public final class LodestoneNetworking {
    private static volatile NetworkTransport transport;

    private LodestoneNetworking() {
    }

    public static void install(NetworkTransport networkTransport) {
        if (networkTransport == null) {
            throw new NullPointerException("networkTransport");
        }
        synchronized (LodestoneNetworking.class) {
            if (transport != null) {
                throw new IllegalStateException("Lodestone network transport is already installed");
            }
            transport = networkTransport;
        }
    }

    public static void sendToAllPlayers(CustomPacketPayload payload) {
        getTransport().sendToAllPlayers(payload);
    }

    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        getTransport().sendToPlayer(player, payload);
    }

    public static void sendToPlayersTrackingChunk(ServerLevel level, ChunkPos chunkPos, CustomPacketPayload payload) {
        getTransport().sendToPlayersTrackingChunk(level, chunkPos, payload);
    }

    private static NetworkTransport getTransport() {
        NetworkTransport installedTransport = transport;
        if (installedTransport == null) {
            throw new IllegalStateException("Lodestone network transport has not been installed");
        }
        return installedTransport;
    }
}
