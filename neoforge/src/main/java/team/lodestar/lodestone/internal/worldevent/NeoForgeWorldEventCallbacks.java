package team.lodestar.lodestone.internal.worldevent;

import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import team.lodestar.lodestone.events.types.worldevent.WorldEventCreationEvent;
import team.lodestar.lodestone.events.types.worldevent.WorldEventDiscardEvent;
import team.lodestar.lodestone.events.types.worldevent.WorldEventTickEvent;
import team.lodestar.lodestone.modules.toolkit.worldevent.WorldEventInstance;

public final class NeoForgeWorldEventCallbacks implements WorldEventCallbacks {
    @Override
    public void creation(WorldEventInstance instance, Level level) {
        NeoForge.EVENT_BUS.post(new WorldEventCreationEvent(instance, level));
    }

    @Override
    public void tick(WorldEventInstance instance, Level level) {
        NeoForge.EVENT_BUS.post(new WorldEventTickEvent(instance, level));
    }

    @Override
    public void discard(WorldEventInstance instance, Level level) {
        NeoForge.EVENT_BUS.post(new WorldEventDiscardEvent(instance, level));
    }
}
