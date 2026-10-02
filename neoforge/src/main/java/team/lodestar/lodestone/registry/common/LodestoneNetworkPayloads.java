package team.lodestar.lodestone.registry.common;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamMemberEncoder;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import team.lodestar.lodestone.internal.LodestoneCommon;
import team.lodestar.lodestone.systems.network.LodestoneNetworkPayloadData;
import team.lodestar.lodestone.systems.network.OneSidedPayloadData;
import team.lodestar.lodestone.systems.network.TwoSidedPayloadData;
import team.lodestar.lodestone.systems.network.NeoForgePayloadContext;

import java.util.HashMap;
import java.util.function.Function;
import team.lodestar.lodestone.internal.network.ClientPayloadRegistrar;
import team.lodestar.lodestone.internal.network.LodestonePayloads;
import team.lodestar.lodestone.internal.network.PayloadTypes;
import team.lodestar.lodestone.internal.network.PayloadCodecs;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class LodestoneNetworkPayloads {

    public static final LodestonePayloadRegistryHelper LODESTONE_CHANNEL = new LodestonePayloadRegistryHelper(LodestoneCommon.LODESTONE);

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        LodestonePayloads.register(new ClientPayloadRegistrar() {
            @Override
            public <T extends OneSidedPayloadData> void register(String name, Class<T> payloadClass, Function<RegistryFriendlyByteBuf, T> decoder) {
                LODESTONE_CHANNEL.playToClient(registrar, name, payloadClass, decoder::apply);
            }
        });
    }

    /**
     * Network channels function as a database of payload types.
     * Payload Data that extends {@link LodestoneNetworkPayloadData} will use a resource location to first figure out which channel they belong to using the namespace, and the payload type using the path.
     * Lodestone payload data is designed to be extended, see {@link OneSidedPayloadData} and {@link TwoSidedPayloadData}.
     */
    public record LodestonePayloadRegistryHelper(String namespace) {

        public static final HashMap<Class<? extends LodestoneNetworkPayloadData>, CustomPacketPayload.Type<? extends LodestoneNetworkPayloadData>> PAYLOAD_TO_TYPE = PayloadTypes.PAYLOAD_TO_TYPE;

        public <T extends OneSidedPayloadData> void playToClient(PayloadRegistrar registrar, String name, Class<T> clazz, PayloadDataSupplier<T> decoder) {
            CustomPacketPayload.Type<T> type = createPayloadType(clazz, name);
            StreamCodec<RegistryFriendlyByteBuf, T> codec = createStreamCodec(decoder);
            registrar.playToClient(type, codec, (payload, context) -> payload.handle(new NeoForgePayloadContext(context)));
        }

        public <T extends OneSidedPayloadData> void playToServer(PayloadRegistrar registrar, String name, Class<T> clazz, PayloadDataSupplier<T> decoder) {
            CustomPacketPayload.Type<T> type = createPayloadType(clazz, name);
            StreamCodec<RegistryFriendlyByteBuf, T> codec = createStreamCodec(decoder);
            registrar.playToServer(type, codec, (payload, context) -> payload.handle(new NeoForgePayloadContext(context)));
        }

        public <T extends TwoSidedPayloadData> void playBidirectional(PayloadRegistrar registrar, String name, Class<T> clazz, PayloadDataSupplier<T> decoder) {
            CustomPacketPayload.Type<T> type = createPayloadType(clazz, name);

            StreamCodec<RegistryFriendlyByteBuf, T> codec = createStreamCodec(decoder);
            registrar.playBidirectional(type, codec, new DirectionalPayloadHandler<>(
                    (payload, context) -> payload.handleClient(new NeoForgePayloadContext(context)),
                    (payload, context) -> payload.handleServer(new NeoForgePayloadContext(context))));
        }

        public <T extends LodestoneNetworkPayloadData> StreamCodec<RegistryFriendlyByteBuf, T> createStreamCodec(PayloadDataSupplier<T> supplier) {
            return PayloadCodecs.codec(namespace, supplier::deserializePayload);
        }

        public <B extends RegistryFriendlyByteBuf, T extends LodestoneNetworkPayloadData> StreamMemberEncoder<B, T> serializePayload() {
            return LodestoneNetworkPayloadData::serialize;
        }

        public <B extends RegistryFriendlyByteBuf, T extends LodestoneNetworkPayloadData> StreamDecoder<B, T> deserializePayload(PayloadDataSupplier<T> supplier) {
            return byteBuf -> {
                try {
                    return supplier.deserializePayload(byteBuf);
                } catch (Exception e) {
                    throw new RuntimeException("Couldn't decode payload type from channel " + namespace, e);
                }
            };
        }

        public <T extends LodestoneNetworkPayloadData> CustomPacketPayload.Type<T> createPayloadType(Class<T> clazz, String id) {
            return PayloadCodecs.type(namespace, id, clazz);
        }

    }

    public interface PayloadDataSupplier<T extends LodestoneNetworkPayloadData> {
        T deserializePayload(RegistryFriendlyByteBuf byteBuf);
    }
}