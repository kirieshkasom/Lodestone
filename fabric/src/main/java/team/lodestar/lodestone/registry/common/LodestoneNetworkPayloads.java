package team.lodestar.lodestone.registry.common;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import team.lodestar.lodestone.internal.LodestoneCommon;
import team.lodestar.lodestone.internal.network.PayloadCodecs;
import team.lodestar.lodestone.internal.network.PayloadTypes;
import team.lodestar.lodestone.systems.network.LodestoneNetworkPayloadData;
import team.lodestar.lodestone.systems.network.OneSidedPayloadData;

import java.util.HashMap;
import java.util.function.Function;
import team.lodestar.lodestone.internal.network.ClientPayloadRegistrar;
import team.lodestar.lodestone.internal.network.LodestonePayloads;

public final class LodestoneNetworkPayloads {
    public static final LodestonePayloadRegistryHelper LODESTONE_CHANNEL = new LodestonePayloadRegistryHelper(LodestoneCommon.LODESTONE);

    public static void register() {
        LodestonePayloads.register(new ClientPayloadRegistrar() {
            @Override
            public <T extends OneSidedPayloadData> void register(String name, Class<T> payloadClass, Function<RegistryFriendlyByteBuf, T> decoder) {
                LODESTONE_CHANNEL.playToClient(name, payloadClass, decoder::apply);
            }
        });
    }

    public record LodestonePayloadRegistryHelper(String namespace) {
        public static final HashMap<Class<? extends LodestoneNetworkPayloadData>, CustomPacketPayload.Type<? extends LodestoneNetworkPayloadData>> PAYLOAD_TO_TYPE = PayloadTypes.PAYLOAD_TO_TYPE;

        public <T extends OneSidedPayloadData> void playToClient(String name, Class<T> payloadClass, PayloadDataSupplier<T> decoder) {
            PayloadTypeRegistry.playS2C().register(createPayloadType(payloadClass, name), createStreamCodec(decoder));
        }

        public <T extends LodestoneNetworkPayloadData> CustomPacketPayload.Type<T> createPayloadType(Class<T> payloadClass, String name) {
            return PayloadCodecs.type(namespace, name, payloadClass);
        }

        public <T extends LodestoneNetworkPayloadData> StreamCodec<RegistryFriendlyByteBuf, T> createStreamCodec(PayloadDataSupplier<T> decoder) {
            return PayloadCodecs.codec(namespace, decoder::deserializePayload);
        }
    }

    public interface PayloadDataSupplier<T extends LodestoneNetworkPayloadData> {
        T deserializePayload(RegistryFriendlyByteBuf buffer);
    }
}
