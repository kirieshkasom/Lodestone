package team.lodestar.lodestone.mixin.client;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.lodestar.lodestone.modules.rendering.LodestoneRenderingSystem;

@Mixin(FogRenderer.class)
public abstract class FabricFogCacheMixin {
    @Shadow
    private static float fogRed;
    @Shadow
    private static float fogGreen;
    @Shadow
    private static float fogBlue;

    @Inject(method = "setupColor", at = @At("TAIL"))
    private static void lodestone$cacheFogColor(CallbackInfo ci) {
        LodestoneRenderingSystem.cacheFogColors(fogRed, fogGreen, fogBlue);
    }

    @Inject(method = "setupFog", at = @At("TAIL"))
    private static void lodestone$cacheFogData(CallbackInfo ci) {
        FogShape shape = RenderSystem.getShaderFogShape();
        LodestoneRenderingSystem.cacheFogData(RenderSystem.getShaderFogStart(), RenderSystem.getShaderFogEnd(), shape);
    }
}
