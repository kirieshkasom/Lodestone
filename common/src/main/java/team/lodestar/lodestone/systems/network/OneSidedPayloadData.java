package team.lodestar.lodestone.systems.network;

import team.lodestar.lodestone.internal.network.PayloadContext;

public abstract class OneSidedPayloadData extends LodestoneNetworkPayloadData {
    public abstract void handle(PayloadContext context);
}
