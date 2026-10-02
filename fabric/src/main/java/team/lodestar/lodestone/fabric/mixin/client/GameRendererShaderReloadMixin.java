package team.lodestar.lodestone.fabric.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.lodestar.lodestone.registry.client.LodestoneShaders;
import team.lodestar.lodestone.systems.rendering.shader.ShaderRegistrar;

import java.util.List;
import java.util.function.Consumer;

@Mixin(GameRenderer.class)
public class GameRendererShaderReloadMixin {
    @Inject(
            method = "reloadShaders(Lnet/minecraft/server/packs/resources/ResourceProvider;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/GameRenderer;loadBlurEffect(Lnet/minecraft/server/packs/resources/ResourceProvider;)V"
            )
    )
    private void lodestone$registerShaders(ResourceProvider resources, CallbackInfo ci, @Local(ordinal = 1) List<Pair<ShaderInstance, Consumer<ShaderInstance>>> shaders) {
        ShaderRegistrar registrar = new ShaderRegistrar(resources, (shader, onReload) -> shaders.add(Pair.of(shader, onReload)));
        LodestoneShaders.shaderRegistry(registrar);
    }
}
