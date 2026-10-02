package team.lodestar.lodestone.fabric;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import team.lodestar.lodestone.internal.network.NetworkTransport;

import java.util.function.Supplier;

public final class FabricNetworkTransport implements NetworkTransport {
    private final Supplier<MinecraftServer> serverSupplier;

    public FabricNetworkTransport(Supplier<MinecraftServer> serverSupplier) {
        if (serverSupplier == null) {
            throw new NullPointerException("serverSupplier");
        }
        this.serverSupplier = serverSupplier;
    }

    @Override
    public void sendToAllPlayers(CustomPacketPayload payload) {
        MinecraftServer server = serverSupplier.get();
        if (server == null) {
            throw new IllegalStateException("Minecraft server is not available");
        }
        for (ServerPlayer player : PlayerLookup.all(server)) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    @Override
    public void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        ServerPlayNetworking.send(player, payload);
    }

    @Override
    public void sendToPlayersTrackingChunk(ServerLevel level, ChunkPos chunkPos, CustomPacketPayload payload) {
        for (ServerPlayer player : PlayerLookup.tracking(level, chunkPos)) {
            ServerPlayNetworking.send(player, payload);
        }
    }
}
