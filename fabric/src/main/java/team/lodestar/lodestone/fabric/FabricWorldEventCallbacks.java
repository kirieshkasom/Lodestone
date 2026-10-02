package team.lodestar.lodestone.fabric;

import net.minecraft.world.level.Level;
import team.lodestar.lodestone.fabric.events.FabricWorldEventEvents;
import team.lodestar.lodestone.internal.worldevent.WorldEventCallbacks;
import team.lodestar.lodestone.modules.toolkit.worldevent.WorldEventInstance;

public final class FabricWorldEventCallbacks implements WorldEventCallbacks {
    @Override
    public void creation(WorldEventInstance instance, Level level) {
        FabricWorldEventEvents.EVENT.invoker().creation(instance, level);
    }

    @Override
    public void tick(WorldEventInstance instance, Level level) {
        FabricWorldEventEvents.EVENT.invoker().tick(instance, level);
    }

    @Override
    public void discard(WorldEventInstance instance, Level level) {
        FabricWorldEventEvents.EVENT.invoker().discard(instance, level);
    }
}
