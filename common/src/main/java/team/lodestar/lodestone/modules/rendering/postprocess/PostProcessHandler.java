package team.lodestar.lodestone.modules.rendering.postprocess;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.joml.Matrix4f;
import team.lodestar.lodestone.modules.rendering.RenderPhase;
import team.lodestar.lodestone.systems.asset.ReloadListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles world-space post-processing.
 * Based on vanilla {@link net.minecraft.client.renderer.PostChain} system, but allows the shader to access the world depth buffer.
 */
public class PostProcessHandler {
    private static final List<PostProcessor> instances = new ArrayList<>();
    private static boolean didCopyDepth = false;
    private static final ReloadListener reloadListener = new ReloadListener(() -> RenderSystem.recordRenderCall(() -> instances.forEach(PostProcessor::init)));

    /**
     * Add an {@link PostProcessor} for it to be handled automatically.
     * IMPORTANT: processors has to be added in the right order!!!
     * There's no way of getting an instance, so you need to keep the instance yourself.
     */
    public static void addInstance(PostProcessor instance) {
        instances.add(instance);
    }

    public static void render() {
        instances.forEach(PostProcessor::applyPostProcess);
        Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
    }

    public static void copyDepthBuffer() {
        if (didCopyDepth) return;
        instances.forEach(PostProcessor::copyDepthBuffer);
        didCopyDepth = true;
    }

    public static void resize(int width, int height) {
        instances.forEach(i -> i.resize(width, height));
    }

    public static void renderPhase(RenderPhase phase, Matrix4f modelViewMatrix) {
        if (phase == RenderPhase.AFTER_PARTICLES) {
            PostProcessor.viewModelMatrix = new Matrix4f(modelViewMatrix);
        }
        if (phase == RenderPhase.AFTER_LEVEL) {
            copyDepthBuffer(); // copy the depth buffer if the mixin didn't trigger

            render();

            didCopyDepth = false; // reset for next frame
        }
    }

    public static ResourceManagerReloadListener getReloadListener() {
        return reloadListener;
    }
}
