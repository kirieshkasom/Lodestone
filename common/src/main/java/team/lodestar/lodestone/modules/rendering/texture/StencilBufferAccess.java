package team.lodestar.lodestone.modules.rendering.texture;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_DEPTH32F_STENCIL8;
import static org.lwjgl.opengl.GL30.GL_DEPTH_STENCIL;
import static org.lwjgl.opengl.GL30.GL_DEPTH_STENCIL_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_FLOAT_32_UNSIGNED_INT_24_8_REV;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_BINDING;
import static org.lwjgl.opengl.GL30.GL_STENCIL_ATTACHMENT;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;

public interface StencilBufferAccess {
    boolean lodestone$isStencilEnabled();

    void lodestone$setStencilEnabled(boolean enabled);

    static boolean isStencilEnabled(RenderTarget target) {
        return target instanceof StencilBufferAccess access && access.lodestone$isStencilEnabled();
    }

    static void enableStencil(RenderTarget target) {
        if (!(target instanceof StencilBufferAccess access) || access.lodestone$isStencilEnabled()) {
            return;
        }
        if (StencilPlatform.enableNative(target)) {
            return;
        }
        access.lodestone$setStencilEnabled(true);
        target.resize(target.width, target.height, Minecraft.ON_OSX);
    }

    static void attachStencil(RenderTarget target) {
        RenderSystem.assertOnRenderThreadOrInit();
        int depthTextureId = target.getDepthTextureId();
        if (depthTextureId <= 0) {
            return;
        }
        allocateStencilTexture(target);
        attachStencilTexture(target);
    }

    static void attachStencilTexture(RenderTarget target) {
        RenderSystem.assertOnRenderThreadOrInit();
        int depthTextureId = target.getDepthTextureId();
        if (depthTextureId <= 0) {
            return;
        }
        int framebufferId = GL11.glGetInteger(GL_FRAMEBUFFER_BINDING);
        GlStateManager._glBindFramebuffer(GL_FRAMEBUFFER, target.frameBufferId);
        GlStateManager._glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_TEXTURE_2D, depthTextureId, 0);
        GlStateManager._glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTextureId, 0);
        GlStateManager._glFramebufferTexture2D(GL_FRAMEBUFFER, GL_STENCIL_ATTACHMENT, GL_TEXTURE_2D, depthTextureId, 0);
        GlStateManager._glBindFramebuffer(GL_FRAMEBUFFER, framebufferId);
    }

    private static void allocateStencilTexture(RenderTarget target) {
        int textureId = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        GlStateManager._bindTexture(target.getDepthTextureId());
        GlStateManager._texParameter(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        GlStateManager._texParameter(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        GlStateManager._texParameter(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        GlStateManager._texParameter(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        GlStateManager._texImage2D(GL_TEXTURE_2D, 0, GL_DEPTH32F_STENCIL8, target.width, target.height, 0, GL_DEPTH_STENCIL, GL_FLOAT_32_UNSIGNED_INT_24_8_REV, null);
        GlStateManager._bindTexture(textureId);
    }
}
