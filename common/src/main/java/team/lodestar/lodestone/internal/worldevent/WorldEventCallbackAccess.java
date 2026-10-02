package team.lodestar.lodestone.internal.worldevent;

import net.minecraft.world.level.Level;
import team.lodestar.lodestone.modules.toolkit.worldevent.WorldEventInstance;

import java.util.Objects;

public final class WorldEventCallbackAccess {
    private static final WorldEventCallbacks NOOP = new WorldEventCallbacks() {
        @Override
        public void creation(WorldEventInstance instance, Level level) {
        }

        @Override
        public void tick(WorldEventInstance instance, Level level) {
        }

        @Override
        public void discard(WorldEventInstance instance, Level level) {
        }
    };
    private static volatile WorldEventCallbacks callbacks;

    private WorldEventCallbackAccess() {
    }

    public static synchronized void install(WorldEventCallbacks installedCallbacks) {
        if (callbacks != null) {
            throw new IllegalStateException("World event callbacks are already installed");
        }
        callbacks = Objects.requireNonNull(installedCallbacks);
    }

    public static WorldEventCallbacks callbacks() {
        WorldEventCallbacks current = callbacks;
        return current == null ? NOOP : current;
    }
}
