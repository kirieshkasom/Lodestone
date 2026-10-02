package team.lodestar.lodestone.mixin.client;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.preprocessor.GlslPreprocessor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.FileUtil;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.util.GsonHelper;
import net.neoforged.neoforge.client.ClientHooks;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.lodestar.lodestone.LodestoneLib;
import team.lodestar.lodestone.systems.rendering.shader.IShaderInstance;
import team.lodestar.lodestone.systems.rendering.shader.LodestoneProgram;
import team.lodestar.lodestone.systems.rendering.shader.SamplerType;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mixin(ShaderInstance.class)
public class ShaderInstanceMixin implements IShaderInstance {
    private LodestoneProgram geometryProgram;

    @Unique
    private Map<String, SamplerType> samplerTypeMap = Maps.newHashMap();

    @Redirect(method = "parseSamplerNode", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    private boolean samplerDimension(List<String> samplerNames, Object s, @Local JsonElement json) {
        String name = s.toString();
        if (json.getAsJsonObject().has("type")) {
            String type1 = json.getAsJsonObject().get("type").getAsString();
            SamplerType type = SamplerType.fromString(type1);
            if (type == null) {
                LodestoneLib.LOGGER.warn("Unknown sampler type: " + type1);
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

    @Inject(method = "<init>(Lnet/minecraft/server/packs/resources/ResourceProvider;Lnet/minecraft/resources/ResourceLocation;Lcom/mojang/blaze3d/vertex/VertexFormat;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/GsonHelper;getAsString(Lcom/google/gson/JsonObject;Ljava/lang/String;)Ljava/lang/String;",
                    ordinal = 1
            )
    )
    private void lodestone$setGeometryProgram(ResourceProvider resourceProvider, ResourceLocation shaderLocation, VertexFormat vertexFormat, CallbackInfo ci, @Local(ordinal = 0) JsonObject json) throws IOException {
        if (json.has("geometry")) {
            String geometry = GsonHelper.getAsString(json, "geometry");
            this.geometryProgram = lodestone$getOrCreate(resourceProvider, LodestoneProgram.Type.GEOMETRY, geometry);
            LodestoneLib.LOGGER.info("Loaded geometry program: " + geometry);
        }
    }

    @Inject(method = "attachToProgram", at = @At("TAIL"))
    private void attachToProgram(CallbackInfo ci) {
        if (this.geometryProgram != null) {
            this.geometryProgram.attachToShader((ShaderInstance) (Object) this);
        }
    }

    @Inject(method = "close", at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/shaders/ProgramManager;releaseProgram(Lcom/mojang/blaze3d/shaders/Shader;)V",
            shift = At.Shift.BEFORE
    ))
    public void close(CallbackInfo ci) {
        if (this.geometryProgram != null) {
            this.geometryProgram.close();
            LodestoneLib.LOGGER.info("Closed geometry program: " + this.geometryProgram.getName());
            this.geometryProgram = null;
        }
    }

    @Unique
    private static LodestoneProgram lodestone$getOrCreate(final ResourceProvider resourceProvider, LodestoneProgram.Type programType, String name) throws IOException {
        LodestoneProgram program1 = programType.getPrograms().get(name);
        LodestoneProgram program;
        if (program1 == null) {
            ResourceLocation loc = ResourceLocation.parse(name);
            String var10000 = loc.getPath();
            String s = "shaders/core/" + var10000 + programType.getExtension();
            ResourceLocation resourcelocation = ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), s);
            Resource resource = resourceProvider.getResourceOrThrow(resourcelocation);

            try (InputStream inputstream = resource.open()) {
                final String s1 = FileUtil.getFullResourcePath(s);
                program = LodestoneProgram.compileShader(programType, name, inputstream, resource.sourcePackId(), new GlslPreprocessor() {
                    private final Set<String> importedPaths = Sets.newHashSet();

                    public String applyImport(boolean p_173374_, String p_173375_) {
                        ResourceLocation resourcelocation = ClientHooks.getShaderImportLocation(s1, p_173374_, p_173375_);
                        if (!this.importedPaths.add(resourcelocation.toString())) {
                            return null;
                        } else {
                            try (Reader reader = resourceProvider.openAsReader(resourcelocation)) {
                                return IOUtils.toString(reader);
                            } catch (IOException ioexception) {
                                LodestoneLib.LOGGER.error("Could not open GLSL import {}: {}", resourcelocation, ioexception.getMessage());
                                return "#error " + ioexception.getMessage();
                            }
                        }
                    }
                });
            }
        } else {
            program = program1;
        }

        return program;
    }

    @Override
    public @Nullable LodestoneProgram getGeometryProgram() {
        return this.geometryProgram;
    }
}
