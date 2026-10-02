package team.lodestar.lodestone.fabric.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.level.Level;
import team.lodestar.lodestone.internal.worldevent.WorldEventCallbacks;
import team.lodestar.lodestone.modules.toolkit.worldevent.WorldEventInstance;

public final class FabricWorldEventEvents {
    public static final Event<WorldEventCallbacks> EVENT = EventFactory.createArrayBacked(WorldEventCallbacks.class, callbacks -> new WorldEventCallbacks() {
        @Override
        public void creation(WorldEventInstance instance, Level level) {
            for (WorldEventCallbacks callback : callbacks) {
                callback.creation(instance, level);
            }
        }

        @Override
        public void tick(WorldEventInstance instance, Level level) {
            for (WorldEventCallbacks callback : callbacks) {
                callback.tick(instance, level);
            }
        }

        @Override
        public void discard(WorldEventInstance instance, Level level) {
            for (WorldEventCallbacks callback : callbacks) {
                callback.discard(instance, level);
            }
        }
    });

    private FabricWorldEventEvents() {
    }
}
