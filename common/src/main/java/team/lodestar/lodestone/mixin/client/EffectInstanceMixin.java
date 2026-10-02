package team.lodestar.lodestone.mixin.client;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import team.lodestar.lodestone.internal.LodestoneCommon;
import team.lodestar.lodestone.systems.rendering.shader.SamplerType;

import java.util.List;
import java.util.Map;

@Mixin(EffectInstance.class)
public abstract class EffectInstanceMixin {

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/ResourceLocation;withDefaultNamespace(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"), require = 0)
    private ResourceLocation lodestone$effectLocation(String path) {
        return lodestone$programLocation(path);
    }

    @Redirect(method = "getOrCreate", at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/ResourceLocation;withDefaultNamespace(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"), require = 0)
    private static ResourceLocation lodestone$effectProgramLocation(String path) {
        return lodestone$programLocation(path);
    }

    @Unique
    private static ResourceLocation lodestone$programLocation(String path) {
        String prefix = "shaders/program/";
        ResourceLocation location = ResourceLocation.parse(path.substring(prefix.length()));
        return location.withPath(value -> prefix + value);
    }

    @Unique
    private Map<String, SamplerType> samplerTypeMap = Maps.newHashMap();

    @Redirect(method = "parseSamplerNode", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    private boolean samplerDimension(List<String> samplerNames, Object s, @Local JsonElement json) {
        String name = s.toString();
        if (json.getAsJsonObject().has("type")) {
            String type1 = json.getAsJsonObject().get("type").getAsString();
            SamplerType type = SamplerType.fromString(type1);
            if (type == null) {
                LodestoneCommon.LOGGER.warn("Unknown sampler type: " + type1);
            } else {
                samplerTypeMap.put(name, type);
            }
        }
        return samplerNames.add(name);
    }

    @Redirect(method = "apply", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;bindTexture(I)V"))
    private void bindTex(int textureBinding, @Local String s) {
        if (samplerTypeMap.containsKey(s)) {
            SamplerType type = samplerTypeMap.get(s);
            bindTexture(type.getGlType(), textureBinding);
        } else {
            RenderSystem.bindTexture(textureBinding);
        }
    }

    private void bindTexture(int samplerType, int texture) {
        GlStateManager.TextureState activeTexture = GlStateManager.TEXTURES[GlStateManager.activeTexture];
        if (texture != activeTexture.binding) {
            activeTexture.binding = texture;
            GL11.glBindTexture(samplerType, texture);
        }
    }
}
