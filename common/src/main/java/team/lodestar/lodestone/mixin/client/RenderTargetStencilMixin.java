package team.lodestar.lodestone.mixin.client;

import com.mojang.blaze3d.pipeline.RenderTarget;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.lodestar.lodestone.modules.rendering.texture.StencilBufferAccess;
import team.lodestar.lodestone.modules.rendering.texture.StencilPlatform;

@Mixin(RenderTarget.class)
public abstract class RenderTargetStencilMixin implements StencilBufferAccess {
    @Unique
    private boolean lodestone$stencilEnabled;
    @Unique
    private int lodestone$stencilClearValue;
    @Unique
    private int lodestone$stencilFrontWriteMask;
    @Unique
    private int lodestone$stencilBackWriteMask;

    @Override
    public boolean lodestone$isStencilEnabled() {
        return this.lodestone$stencilEnabled;
    }

    @Override
    public void lodestone$setStencilEnabled(boolean enabled) {
        this.lodestone$stencilEnabled = enabled;
    }

    @Inject(method = "createBuffers", at = @At("TAIL"))
    private void lodestone$createStencilBuffer(int width, int height, boolean clearError, CallbackInfo ci) {
        if (this.lodestone$stencilEnabled && !StencilPlatform.hasNativeEnabler()) {
            RenderTarget target = (RenderTarget) (Object) this;
            StencilBufferAccess.attachStencil(target);
            target.checkStatus();
            target.clear(clearError);
        }
    }

    @Inject(method = "clear", at = @At("HEAD"))
    private void lodestone$prepareStencilClear(boolean clearError, CallbackInfo ci) {
        if (this.lodestone$stencilEnabled) {
            this.lodestone$stencilClearValue = GL11.glGetInteger(GL11.GL_STENCIL_CLEAR_VALUE);
            this.lodestone$stencilFrontWriteMask = GL11.glGetInteger(GL11.GL_STENCIL_WRITEMASK);
            this.lodestone$stencilBackWriteMask = GL11.glGetInteger(GL20.GL_STENCIL_BACK_WRITEMASK);
            GL11.glClearStencil(0);
            GL11.glStencilMask(0xFF);
        }
    }

    @Inject(method = "clear", at = @At("TAIL"))
    private void lodestone$restoreStencilClearState(boolean clearError, CallbackInfo ci) {
        if (this.lodestone$stencilEnabled) {
            GL11.glClearStencil(this.lodestone$stencilClearValue);
            GL20.glStencilMaskSeparate(GL11.GL_FRONT, this.lodestone$stencilFrontWriteMask);
            GL20.glStencilMaskSeparate(GL11.GL_BACK, this.lodestone$stencilBackWriteMask);
        }
    }

    @ModifyArg(method = "clear", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_clear(IZ)V"), index = 0)
    private int lodestone$clearStencilBuffer(int mask) {
        return this.lodestone$stencilEnabled ? mask | GL11.GL_STENCIL_BUFFER_BIT : mask;
    }

    @Inject(method = "enableStencil", at = @At("HEAD"), require = 0)
    private void lodestone$trackNativeStencilEnable(CallbackInfo ci) {
        this.lodestone$stencilEnabled = true;
    }
}
