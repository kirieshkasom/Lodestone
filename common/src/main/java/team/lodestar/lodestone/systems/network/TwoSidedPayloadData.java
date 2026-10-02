package team.lodestar.lodestone.systems.network;

import team.lodestar.lodestone.internal.network.PayloadContext;

public abstract class TwoSidedPayloadData extends LodestoneNetworkPayloadData {
    public abstract void handleClient(PayloadContext context);

    public abstract void handleServer(PayloadContext context);
}
