package team.lodestar.lodestone.internal.worldevent;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;
import java.util.Objects;

public final class WorldEventCommandContext {
    private static volatile Supplier<MinecraftServer> server = () -> null;
    private static volatile Supplier<Level> clientLevel = () -> null;

    private WorldEventCommandContext() {
    }

    public static void serverSupplier(Supplier<MinecraftServer> supplier) {
        server = Objects.requireNonNull(supplier);
    }

    public static void clientLevelSupplier(Supplier<Level> supplier) {
        clientLevel = Objects.requireNonNull(supplier);
    }

    public static MinecraftServer server() {
        return server.get();
    }

    public static Level clientLevel() {
        return clientLevel.get();
    }
}
