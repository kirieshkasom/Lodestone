package team.lodestar.lodestone.modules.rendering.postprocess;

import com.google.gson.JsonParseException;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import team.lodestar.lodestone.LodestoneLib;
import team.lodestar.lodestone.systems.rendering.LodestoneRenderSystem;

import java.io.IOException;

import static com.mojang.blaze3d.platform.GlConst.GL_DRAW_FRAMEBUFFER;

/**
 * Abstract world space post-process pass.
 * If your shader needs the world space depth buffer, add a target called "depthMain" for it to automatically
 * @see <a href="https://github.com/LodestarMC/Lodestone/wiki/Post-Processing-Shaders">Lodestone Post Processing Wiki</a>
 */
public abstract class PostProcessor {
    /**
     * Being updated every frame before calling applyPostProcess() by PostProcessHandler
     */
    public static Matrix4f viewModelMatrix;

    private boolean initialized = false;
    protected PostChain postChain;
    protected EffectInstance[] effects;
    private RenderTarget tempDepthBuffer;

    private boolean isActive = true;

    protected double time;

    /**
     * Example: "octus:foo" points to octus:shaders/post/foo.json
     */
    public abstract ResourceLocation getPostChainLocation();

    public void init() {
        loadPostChain();

        if (postChain != null) {
            tempDepthBuffer = postChain.getTempTarget("depthMain");
        }

        initialized = true;
    }

    /**
     * Load or reload the shader
     */
    public final void loadPostChain() {
        if (postChain != null) {
            postChain.close();
            postChain = null;
        }

        try {
            var mc = Minecraft.getInstance();
            ResourceLocation file = getPostChainLocation();
            file = ResourceLocation.fromNamespaceAndPath(file.getNamespace(), "shaders/post/" + file.getPath() + ".json");
            postChain = new PostChain(
                    mc.getTextureManager(),
                    mc.getResourceManager(),
                    mc.getMainRenderTarget(),
                    file
            );
            postChain.resize(mc.getWindow().getWidth(), mc.getWindow().getHeight());
            effects = postChain.passes.stream().map(PostPass::getEffect).toArray(EffectInstance[]::new);
        } catch (IOException | JsonParseException e) {
            LodestoneLib.LOGGER.error("Failed to load post-processing shader: ", e);
        }
    }

    public void copyDepthBuffer() {
        if (isActive) {
            if (postChain == null || tempDepthBuffer == null) return;

            tempDepthBuffer.copyDepthFrom(Minecraft.getInstance().getMainRenderTarget());

            // rebind the main framebuffer so that we don't mess up other things
            GlStateManager._glBindFramebuffer(GL_DRAW_FRAMEBUFFER, Minecraft.getInstance().getMainRenderTarget().frameBufferId);
        }
    }

    public void resize(int width, int height) {
        if (postChain != null) {
            postChain.resize(width, height);
            if (tempDepthBuffer != null)
                tempDepthBuffer.resize(width, height, Minecraft.ON_OSX);
        }
    }

    private void applyDefaultUniforms() {
        var MC = Minecraft.getInstance();
        for (EffectInstance e : effects) {
            e.safeGetUniform("time").set((float) time);
            e.safeGetUniform("cameraPos").set(new Vector3f(MC.gameRenderer.getMainCamera().getPosition().toVector3f()));
            e.safeGetUniform("lookVector").set(MC.gameRenderer.getMainCamera().getLookVector());
            e.safeGetUniform("upVector").set(MC.gameRenderer.getMainCamera().getUpVector());
            e.safeGetUniform("leftVector").set(MC.gameRenderer.getMainCamera().getLeftVector());
            e.safeGetUniform("invViewMat").set(PostProcessor.viewModelMatrix.invert(new Matrix4f()));
            e.safeGetUniform("invProjMat").set(RenderSystem.getProjectionMatrix().invert(new Matrix4f()));
            e.safeGetUniform("nearPlaneDistance").set(GameRenderer.PROJECTION_Z_NEAR);
            e.safeGetUniform("farPlaneDistance").set(MC.gameRenderer.getDepthFar());
            e.safeGetUniform("fov").set((float) Math.toRadians(MC.gameRenderer.getFov(MC.gameRenderer.getMainCamera(), MC.getTimer().getGameTimeDeltaPartialTick(false), true)));
            e.safeGetUniform("aspectRatio").set((float) MC.getWindow().getWidth() / (float) MC.getWindow().getHeight());
            e.safeGetUniform("bobOffset").set(PostProcessor.viewModelMatrix.invert(new Matrix4f()).transformPosition(LodestoneRenderSystem.getViewBobOffset(), new Vector3f()));
        }
    }

    public void applyPostProcess() {
        if (isActive) {
            if (!initialized)
                init();

            if (postChain != null) {
                var mc = Minecraft.getInstance();
                float partialTicks = mc.getTimer().getGameTimeDeltaPartialTick(false);
                time += partialTicks / 20.0;

                beforeProcess(viewModelMatrix);

                applyDefaultUniforms();
                if (!isActive) return;
                postChain.process(partialTicks);

                GlStateManager._glBindFramebuffer(GL_DRAW_FRAMEBUFFER, mc.getMainRenderTarget().frameBufferId);
                afterProcess();
            }
        }
    }

    /**
     * Set uniforms and bind textures here
     */
    public abstract void beforeProcess(Matrix4f viewModelMatrix);

    /**
     * Unbind textures
     */
    public abstract void afterProcess();

    public void setActive(boolean active) {
        this.isActive = active;

        if (!active)
            time = 0.0;
    }

    public final boolean isActive() {
        return isActive;
    }
}
