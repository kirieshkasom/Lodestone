package team.lodestar.lodestone.fabric;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public final class FabricReloadRegistryLookup {
    private static final Map<PreparableReloadListener, HolderLookup.Provider> CONTEXTS = Collections.synchronizedMap(new WeakHashMap<>());

    private FabricReloadRegistryLookup() {
    }

    public static void associate(PreparableReloadListener listener, HolderLookup.Provider registries) {
        CONTEXTS.put(listener, registries);
    }

    public static HolderLookup.Provider lookup(PreparableReloadListener listener) {
        HolderLookup.Provider context = CONTEXTS.get(listener);
        return context == null ? RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY) : context;
    }
}
