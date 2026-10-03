package team.lodestar.lodestone.modules.toolkit.reload_listener;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.Objects;
import java.util.function.Function;

/**
 * Supplies the registry context used by codec reload listeners through the active loader.
 * A listener may override the platform context with its own lookup supplier.
 */
public final class ReloadRegistryLookup {
    private static Function<PreparableReloadListener, HolderLookup.Provider> lookup = listener -> RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);

    private ReloadRegistryLookup() {
    }

    public static void install(Function<PreparableReloadListener, HolderLookup.Provider> resolver) {
        lookup = Objects.requireNonNull(resolver);
    }

    public static HolderLookup.Provider lookup(PreparableReloadListener listener) {
        return Objects.requireNonNull(lookup.apply(listener));
    }
}
