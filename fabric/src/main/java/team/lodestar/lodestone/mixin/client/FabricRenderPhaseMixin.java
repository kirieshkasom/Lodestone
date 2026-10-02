package team.lodestar.lodestone.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.lodestar.lodestone.modules.rendering.LodestoneRenderingSystem;
import team.lodestar.lodestone.modules.rendering.RenderPhase;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;

@Mixin(LevelRenderer.class)
public abstract class FabricRenderPhaseMixin {
    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderSky(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;FLnet/minecraft/client/Camera;ZLjava/lang/Runnable;)V", shift = At.Shift.AFTER))
    private void lodestone$afterSky(DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
        LodestoneRenderingSystem.renderPhase(RenderPhase.AFTER_SKY, new PoseStack(), camera, deltaTracker.getGameTimeDeltaPartialTick(false), modelViewMatrix, projectionMatrix);
    }

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/ParticleEngine;render(Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/Camera;F)V", shift = At.Shift.AFTER))
    private void lodestone$afterParticles(DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
        LodestoneRenderingSystem.renderPhase(RenderPhase.AFTER_PARTICLES, new PoseStack(), camera, deltaTracker.getGameTimeDeltaPartialTick(false), modelViewMatrix, projectionMatrix);
    }

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderSnowAndRain(Lnet/minecraft/client/renderer/LightTexture;FDDD)V", shift = At.Shift.AFTER))
    private void lodestone$afterWeather(DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
        LodestoneRenderingSystem.renderPhase(RenderPhase.AFTER_WEATHER, new PoseStack(), camera, deltaTracker.getGameTimeDeltaPartialTick(false), modelViewMatrix, projectionMatrix);
    }

    @Inject(method = "renderLevel", at = @At(value = "CONSTANT", args = "stringValue=destroyProgress"))
    private void lodestone$afterBlockEntities(DeltaTracker deltaTracker, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
        LodestoneRenderingSystem.renderPhase(RenderPhase.AFTER_BLOCK_ENTITIES, new PoseStack(), camera, deltaTracker.getGameTimeDeltaPartialTick(false), modelViewMatrix, projectionMatrix);
    }

    @Inject(method = "renderSectionLayer", at = @At("TAIL"))
    private void lodestone$renderSectionPhase(RenderType renderType, double x, double y, double z, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, CallbackInfo ci) {
        RenderPhase phase = null;
        if (renderType == RenderType.solid()) {
            phase = RenderPhase.AFTER_SOLID_BLOCKS;
        } else if (renderType == RenderType.cutoutMipped()) {
            phase = RenderPhase.AFTER_CUTOUT_MIPPED_BLOCKS;
        } else if (renderType == RenderType.cutout()) {
            phase = RenderPhase.AFTER_CUTOUT_BLOCKS;
        } else if (renderType == RenderType.translucent()) {
            phase = RenderPhase.AFTER_TRANSLUCENT_BLOCKS;
        } else if (renderType == RenderType.tripwire()) {
            phase = RenderPhase.AFTER_TRIPWIRE_BLOCKS;
        }
        if (phase != null) {
            Minecraft minecraft = Minecraft.getInstance();
            LodestoneRenderingSystem.renderPhase(phase, new PoseStack(), minecraft.gameRenderer.getMainCamera(), minecraft.getTimer().getGameTimeDeltaPartialTick(false), modelViewMatrix, projectionMatrix);
        }
    }
}
