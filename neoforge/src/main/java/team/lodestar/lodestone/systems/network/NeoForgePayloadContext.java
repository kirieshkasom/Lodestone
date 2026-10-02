package team.lodestar.lodestone.systems.network;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import team.lodestar.lodestone.internal.network.PayloadContext;

public final class NeoForgePayloadContext implements PayloadContext {
    private final IPayloadContext context;

    public NeoForgePayloadContext(IPayloadContext context) {
        this.context = context;
    }

    @Override
    public Player player() {
        return context.player();
    }

    @Override
    public void execute(Runnable task) {
        context.enqueueWork(task);
    }

}
