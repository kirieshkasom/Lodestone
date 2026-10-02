package team.lodestar.lodestone.internal.network;

import net.minecraft.world.entity.player.Player;

public interface PayloadContext {
    Player player();

    void execute(Runnable task);
}
