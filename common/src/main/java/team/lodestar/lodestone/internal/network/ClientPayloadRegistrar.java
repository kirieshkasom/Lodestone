package team.lodestar.lodestone.internal.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import team.lodestar.lodestone.systems.network.OneSidedPayloadData;

import java.util.function.Function;

public interface ClientPayloadRegistrar {
    <T extends OneSidedPayloadData> void register(String name, Class<T> payloadClass, Function<RegistryFriendlyByteBuf, T> decoder);
}
