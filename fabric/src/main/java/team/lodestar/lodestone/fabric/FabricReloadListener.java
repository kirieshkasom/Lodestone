package team.lodestar.lodestone.fabric;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public record FabricReloadListener(ResourceLocation id, PreparableReloadListener listener) implements IdentifiableResourceReloadListener {
    @Override
    public ResourceLocation getFabricId() {
        return id;
    }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager resources, ProfilerFiller preparationProfiler, ProfilerFiller applyProfiler, Executor preparationExecutor, Executor applyExecutor) {
        return listener.reload(barrier, resources, preparationProfiler, applyProfiler, preparationExecutor, applyExecutor);
    }
}
