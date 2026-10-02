package team.lodestar.lodestone.mixin.client;

import net.minecraft.client.renderer.*;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.lodestar.lodestone.modules.rendering.LodestoneRenderingSystem;
import team.lodestar.lodestone.modules.rendering.postprocess.PostProcessHandler;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "net.minecraft.client.renderer.PostChain.process(F)V", ordinal = 1))
    public void lodestone$injectionBeforeTransparencyChainProcess(CallbackInfo ci) {
        PostProcessHandler.copyDepthBuffer();
    }
    @ModifyVariable(method = "renderLevel", at = @At(value = "HEAD"), index = 6, argsOnly = true)
    public Matrix4f lodestone$cacheBlockModelViewMatrix(Matrix4f frustumMatrix) {
        LodestoneRenderingSystem.cacheModelViewMatrix(frustumMatrix);
        return frustumMatrix;
    }
}