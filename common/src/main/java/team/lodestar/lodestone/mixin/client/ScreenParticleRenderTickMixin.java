package team.lodestar.lodestone.mixin.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.lodestar.lodestone.handlers.screenparticle.ScreenParticleHandler;

@Mixin(GameRenderer.class)
public class ScreenParticleRenderTickMixin {
    @Inject(method = "render", at = @At("TAIL"))
    private void lodestone$finishScreenParticleRenderTick(DeltaTracker deltaTracker, boolean bl, CallbackInfo ci) {
        ScreenParticleHandler.renderTick();
    }
}
