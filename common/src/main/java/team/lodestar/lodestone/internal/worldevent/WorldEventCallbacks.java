package team.lodestar.lodestone.internal.worldevent;

import net.minecraft.world.level.Level;
import team.lodestar.lodestone.modules.toolkit.worldevent.WorldEventInstance;

public interface WorldEventCallbacks {
    void creation(WorldEventInstance instance, Level level);

    void tick(WorldEventInstance instance, Level level);

    void discard(WorldEventInstance instance, Level level);
}
