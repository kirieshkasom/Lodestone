package team.lodestar.lodestone.internal.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.HashMap;
import team.lodestar.lodestone.systems.network.LodestoneNetworkPayloadData;

public final class PayloadTypes {
    public static final HashMap<Class<? extends LodestoneNetworkPayloadData>, CustomPacketPayload.Type<? extends LodestoneNetworkPayloadData>> PAYLOAD_TO_TYPE = new HashMap<>();

    private PayloadTypes() {
    }
}
