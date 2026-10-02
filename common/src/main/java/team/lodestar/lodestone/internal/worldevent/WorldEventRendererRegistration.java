package team.lodestar.lodestone.internal.worldevent;

import team.lodestar.lodestone.modules.toolkit.worldevent.WorldEventRenderer;
import team.lodestar.lodestone.modules.toolkit.worldevent.WorldEventType;

import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.Objects;

public final class WorldEventRendererRegistration {
    private static volatile BiConsumer<WorldEventType, Supplier<? extends WorldEventRenderer<?>>> registrar = (type, renderer) -> {
    };
    private static boolean installed;

    private WorldEventRendererRegistration() {
    }

    public static synchronized void install(BiConsumer<WorldEventType, Supplier<? extends WorldEventRenderer<?>>> value) {
        if (installed) {
            throw new IllegalStateException("The world event renderer registrar has already been installed");
        }
        registrar = Objects.requireNonNull(value);
        installed = true;
    }

    public static void register(WorldEventType type, Supplier<? extends WorldEventRenderer<?>> renderer) {
        registrar.accept(type, renderer);
    }
}
