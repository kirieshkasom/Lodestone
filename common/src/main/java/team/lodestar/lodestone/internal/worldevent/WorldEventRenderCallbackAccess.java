package team.lodestar.lodestone.internal.worldevent;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import team.lodestar.lodestone.modules.toolkit.worldevent.WorldEventInstance;
import team.lodestar.lodestone.modules.toolkit.worldevent.WorldEventRenderer;

import java.util.Objects;

public final class WorldEventRenderCallbackAccess {
    private static volatile WorldEventRenderCallback callback;

    private WorldEventRenderCallbackAccess() {
    }

    public static synchronized void install(WorldEventRenderCallback installedCallback) {
        if (callback != null) {
            throw new IllegalStateException("World event render callback is already installed");
        }
        callback = Objects.requireNonNull(installedCallback);
    }

    public static void render(WorldEventInstance instance, WorldEventRenderer<WorldEventInstance> renderer, PoseStack poseStack, MultiBufferSource multiBufferSource, float partialTicks) {
        WorldEventRenderCallback current = callback;
        if (current != null) {
            current.render(instance, renderer, poseStack, multiBufferSource, partialTicks);
        }
    }
}
