package team.lodestar.lodestone.systems.asset;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.jetbrains.annotations.NotNull;

/**
 * A basic object to simplify resource reload listener registration.
 */
public class ReloadListener implements ResourceManagerReloadListener {
    private final Runnable onResourceReload;

    public ReloadListener(Runnable onResourceReload) {
        this.onResourceReload = onResourceReload;
    }

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
        onResourceReload.run();
    }
}