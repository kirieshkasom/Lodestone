package team.lodestar.lodestone.internal.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import team.lodestar.lodestone.systems.network.LodestoneNetworkPayloadData;

import java.util.function.Function;

public final class PayloadCodecs {
    private PayloadCodecs() {
    }

    public static <T extends LodestoneNetworkPayloadData> CustomPacketPayload.Type<T> type(String namespace, String name, Class<T> payloadClass) {
        CustomPacketPayload.Type<T> type = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(namespace, name));
        PayloadTypes.PAYLOAD_TO_TYPE.put(payloadClass, type);
        return type;
    }

    public static <T extends LodestoneNetworkPayloadData> StreamCodec<RegistryFriendlyByteBuf, T> codec(String namespace, Function<RegistryFriendlyByteBuf, T> decoder) {
        return StreamCodec.ofMember(LodestoneNetworkPayloadData::serialize, buffer -> {
            try {
                return decoder.apply(buffer);
            } catch (Exception exception) {
                throw new RuntimeException("Couldn't decode payload type from channel " + namespace, exception);
            }
        });
    }
}
