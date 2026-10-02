package team.lodestar.lodestone.fabric;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import team.lodestar.lodestone.internal.network.PayloadContext;
import team.lodestar.lodestone.internal.network.LodestonePayloads;
import team.lodestar.lodestone.internal.network.ClientPayloadRegistrar;
import team.lodestar.lodestone.internal.network.PayloadTypes;
import team.lodestar.lodestone.systems.network.OneSidedPayloadData;

import java.util.function.Function;

public final class LodestoneFabricNetworking {
    private LodestoneFabricNetworking() {
    }

    public static void installClient() {
        LodestonePayloads.register(new ClientPayloadRegistrar() {
            @Override
            public <T extends OneSidedPayloadData> void register(String name, Class<T> payloadClass, Function<RegistryFriendlyByteBuf, T> decoder) {
                receiver(payloadClass);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private static <T extends OneSidedPayloadData> void receiver(Class<T> payloadClass) {
        CustomPacketPayload.Type<T> type = (CustomPacketPayload.Type<T>) PayloadTypes.PAYLOAD_TO_TYPE.get(payloadClass);
        ClientPlayNetworking.registerGlobalReceiver(type, (payload, context) -> payload.handle(new PayloadContext() {
            @Override
            public Player player() {
                return context.player();
            }

            @Override
            public void execute(Runnable task) {
                context.client().execute(task);
            }
        }));
    }
}
