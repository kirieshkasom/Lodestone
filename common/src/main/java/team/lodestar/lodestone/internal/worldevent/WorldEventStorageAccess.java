package team.lodestar.lodestone.internal.worldevent;

import net.minecraft.world.level.Level;
import team.lodestar.lodestone.modules.toolkit.worldevent.WorldEventAttachment;

import java.util.Objects;

public final class WorldEventStorageAccess {
    private static volatile WorldEventStorage storage;

    private WorldEventStorageAccess() {
    }

    public static synchronized void install(WorldEventStorage worldEventStorage) {
        if (storage != null) {
            throw new IllegalStateException("World event storage is already installed");
        }
        storage = Objects.requireNonNull(worldEventStorage, "worldEventStorage");
    }

    public static WorldEventAttachment get(Level level) {
        WorldEventStorage current = storage;
        if (current == null) {
            throw new IllegalStateException("World event storage has not been installed");
        }
        return current.get(Objects.requireNonNull(level, "level"));
    }
}
