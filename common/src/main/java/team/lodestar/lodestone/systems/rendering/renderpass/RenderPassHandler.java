package team.lodestar.lodestone.systems.rendering.renderpass;

import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

/**
 * Barebones system for early renderpasses before the level is rendered.
 */
public class RenderPassHandler {
    public static final List<BeforeLevelRenderPass> renderPasses = new ArrayList<>();

    public static void registerRenderPass(BeforeLevelRenderPass renderPass) {
        renderPasses.add(renderPass);
    }

    public static void render(DeltaTracker deltaTracker, Camera camera, GameRenderer gameRenderer, Matrix4f viewMat, Matrix4f projMat) {
        for (BeforeLevelRenderPass renderPass : renderPasses) {
            if (renderPass.shouldRender(deltaTracker, camera, gameRenderer, viewMat, projMat)) {
                renderPass.render(deltaTracker, camera, gameRenderer, viewMat, projMat);
            }
        }
    }

    public static void resize(int width, int height) {
        for (BeforeLevelRenderPass renderPass : renderPasses) {
            renderPass.resize(width, height);
        }
    }

    public static void close() {
        for (BeforeLevelRenderPass renderPass : renderPasses) {
            if (renderPass instanceof AutoCloseable ac) {
                try {
                    ac.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
