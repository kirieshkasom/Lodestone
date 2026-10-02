package team.lodestar.lodestone.systems.rendering.renderpass;

import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;

/**
 * An abstract class for custom render passes that are rendered before the level is rendered.
 * <p>
 * These can be useful to render custom effects to {@link RenderTarget}s that may be needed at any stage in the rendering pipeline.
 * <p>
 * Optionally if you implement {@link AutoCloseable}, the render pass will be closed on the render thread when the game is closed, allowing you to free up native resources.
 */
public abstract class BeforeLevelRenderPass {

    public abstract void render(DeltaTracker deltaTracker, Camera camera, GameRenderer gameRenderer, Matrix4f viewMat, Matrix4f projMat);

    public abstract boolean shouldRender(DeltaTracker deltaTracker, Camera camera, GameRenderer gameRenderer, Matrix4f viewMat, Matrix4f projMat);

    public abstract void resize(int width, int height);
}
