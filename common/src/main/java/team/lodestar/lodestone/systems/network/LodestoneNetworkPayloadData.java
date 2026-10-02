package team.lodestar.lodestone.systems.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
import team.lodestar.lodestone.internal.network.PayloadTypes;

public abstract class LodestoneNetworkPayloadData implements CustomPacketPayload {
    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PayloadTypes.PAYLOAD_TO_TYPE.get(getClass());
    }

    public abstract void serialize(RegistryFriendlyByteBuf byteBuf);
}
