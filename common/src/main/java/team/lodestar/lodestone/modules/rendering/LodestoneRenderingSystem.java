package team.lodestar.lodestone.modules.rendering;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL30C;
import team.lodestar.lodestone.helpers.RenderHelper;
import team.lodestar.lodestone.modules.rendering.handlers.ParticleHandler;
import team.lodestar.lodestone.modules.rendering.postprocess.PostProcessHandler;
import team.lodestar.lodestone.modules.rendering.texture.StencilBufferAccess;
import team.lodestar.lodestone.systems.rendering.LodestoneRenderLayer;
import team.lodestar.lodestone.systems.rendering.rendeertype.LodestoneRenderType;
import team.lodestar.lodestone.systems.rendering.shader.ExtendedShaderInstance;
import team.lodestar.lodestone.modules.toolkit.worldevent.WorldEventRenderHandler;

import java.util.Optional;

/**
 * A handler responsible for all the backend rendering processes.
 * To have additive transparency work in a minecraft environment, we need to buffer our rendering till after clouds and water have rendered.
 * This happens for particles, as well as all of our custom RenderTypes
 */
public class LodestoneRenderingSystem {

    public static RenderTarget LODESTONE_DEPTH_CACHE;

    public static LodestoneRenderLayer DEFERRED_RENDER = new LodestoneRenderLayer();
    public static LodestoneRenderLayer LATE_DEFERRED_RENDER = new LodestoneRenderLayer();

    public static Matrix4f MODEL_VIEW;

    public static float FOG_NEAR, FOG_FAR;
    public static float FOG_RED, FOG_GREEN, FOG_BLUE;
    public static FogShape FOG_SHAPE;

    public static void resize(int width, int height) {
        if (LODESTONE_DEPTH_CACHE != null) {
            LODESTONE_DEPTH_CACHE.resize(width, height, Minecraft.ON_OSX);
        }
    }

    public static void render() {
        copyDepthBuffer(LODESTONE_DEPTH_CACHE);
        applyCachedFogData();
        DEFERRED_RENDER.endBatches();
        LATE_DEFERRED_RENDER.endBatches();
        restoreFogData();
    }

    public static void renderPhase(RenderPhase phase, PoseStack poseStack, Camera camera, float partialTicks, Matrix4f modelViewMatrix, Matrix4f projectionMatrix) {
        ParticleHandler.render(phase, partialTicks, modelViewMatrix, projectionMatrix);
        if (phase == RenderPhase.AFTER_SKY) {
            ClientLevel level = Minecraft.getInstance().level;
            if (level != null) {
                WorldEventRenderHandler.renderWorldEvents(level, poseStack, camera, partialTicks);
            }
        }
        if (phase == RenderPhase.AFTER_PARTICLES) {
            PostProcessHandler.renderPhase(phase, modelViewMatrix);
        }
        if (phase == RenderPhase.AFTER_WEATHER) {
            render();
        }
        if (phase == RenderPhase.AFTER_LEVEL) {
            PostProcessHandler.renderPhase(phase, modelViewMatrix);
        }
    }

    public static void cacheModelViewMatrix(Matrix4f modelViewMatrix) {
        MODEL_VIEW = new Matrix4f(modelViewMatrix);
    }

    public static void restoreModelViewMatrix() {
        setModelViewMatrix(MODEL_VIEW);
    }

    public static void clearModelViewMatrix() {
        setModelViewMatrix(new Matrix4f());
    }

    public static void setModelViewMatrix(Matrix4f modelViewMatrix) {
        RenderSystem.getModelViewMatrix().set(modelViewMatrix);
    }

    public static void copyDepthBuffer(RenderTarget tempRenderTarget) {
        setupDepthBuffer();
        enableStencil();
        if (tempRenderTarget == null) return;
        RenderTarget mainRenderTarget = Minecraft.getInstance().getMainRenderTarget();
        tempRenderTarget.copyDepthFrom(mainRenderTarget);
        GlStateManager._glBindFramebuffer(GL30C.GL_DRAW_FRAMEBUFFER, mainRenderTarget.frameBufferId);
    }

    public static void setupDepthBuffer() {
        if (LODESTONE_DEPTH_CACHE == null) {
            LODESTONE_DEPTH_CACHE = new TextureTarget(Minecraft.getInstance().getMainRenderTarget().width, Minecraft.getInstance().getMainRenderTarget().height, true, Minecraft.ON_OSX);
        }
    }

    public static void enableStencil() {
        if (StencilBufferAccess.isStencilEnabled(Minecraft.getInstance().getMainRenderTarget())) {
            StencilBufferAccess.enableStencil(LODESTONE_DEPTH_CACHE);
        }
    }

    public static void cacheFogData(float near, float far, FogShape shape) {
        FOG_NEAR = near;
        FOG_FAR = far;
        FOG_SHAPE = shape;
    }

    public static void cacheFogColors(float red, float green, float blue) {
        FOG_RED = red;
        FOG_GREEN = green;
        FOG_BLUE = blue;
    }

    public static void applyCachedFogData() {
        float[] shaderFogColor = RenderSystem.getShaderFogColor();
        float fogRed = shaderFogColor[0];
        float fogGreen = shaderFogColor[1];
        float fogBlue = shaderFogColor[2];
        float shaderFogStart = RenderSystem.getShaderFogStart();
        float shaderFogEnd = RenderSystem.getShaderFogEnd();
        FogShape shaderFogShape = RenderSystem.getShaderFogShape();

        RenderSystem.setShaderFogStart(FOG_NEAR);
        RenderSystem.setShaderFogEnd(FOG_FAR);
        RenderSystem.setShaderFogShape(FOG_SHAPE);
        RenderSystem.setShaderFogColor(FOG_RED, FOG_GREEN, FOG_BLUE);

        FOG_RED = fogRed;
        FOG_GREEN = fogGreen;
        FOG_BLUE = fogBlue;

        FOG_NEAR = shaderFogStart;
        FOG_FAR = shaderFogEnd;
        FOG_SHAPE = shaderFogShape;
    }

    public static void restoreFogData() {
        RenderSystem.setShaderFogStart(FOG_NEAR);
        RenderSystem.setShaderFogEnd(FOG_FAR);
        RenderSystem.setShaderFogShape(FOG_SHAPE);
        RenderSystem.setShaderFogColor(FOG_RED, FOG_GREEN, FOG_BLUE);
    }

    public static void updateUniforms(RenderType renderType) {
        Optional<ShaderInstance> optional = RenderHelper.getShader(renderType);
        if (optional.isEmpty()) {
            return;
        }
        ShaderInstance shader = optional.get();
        if (renderType instanceof LodestoneRenderType lodestoneRenderType) {
            var data = lodestoneRenderType.getUniformData();
            if (data != null) {
                data.setValues(shader);
            }
        }
        shader.setSampler("SceneDepthBuffer", LodestoneRenderingSystem.LODESTONE_DEPTH_CACHE.getDepthTextureId());
        shader.setSampler("SceneDiffuseBuffer", Minecraft.getInstance().getMainRenderTarget().getColorTextureId());
        shader.safeGetUniform("InvProjMat").set(new Matrix4f(RenderSystem.getProjectionMatrix()).invert());
    }

    public static void resetUniforms(RenderType renderType) {
        var optional = RenderHelper.getShader(renderType);
        if (optional.isEmpty()) {
            return;
        }
        if (!(optional.get() instanceof ExtendedShaderInstance shader)) {
            return;
        }
        shader.applyUniformDefaults();
    }
}
