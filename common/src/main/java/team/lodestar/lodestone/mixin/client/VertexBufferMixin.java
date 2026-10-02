package team.lodestar.lodestone.mixin.client;

import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.lodestar.lodestone.systems.rendering.IVertexBuffer;
import team.lodestar.lodestone.systems.rendering.LodestoneRenderSystem;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL31.*;

@Mixin(VertexBuffer.class)
public abstract class VertexBufferMixin implements IVertexBuffer {
    @Unique
    private Map<Integer, Integer> extraVBOs = new HashMap<>();
    @Override
    public void drawInstanced(int instances) {
        LodestoneRenderSystem.wrap(() -> {
            glDrawElementsInstanced(this.mode.asGLMode, this.indexCount, this.getIndexType().asGLType, 0, instances);
        });
    }

    @Override
    public void drawWithShaderInstanced(Matrix4f modelViewMatrix, Matrix4f projectionMatrix, ShaderInstance shader, int instances) {
        LodestoneRenderSystem.wrap(() -> {
            shader.setDefaultUniforms(this.mode, modelViewMatrix, projectionMatrix, Minecraft.getInstance().getWindow());
            shader.apply();
            this.drawInstanced(instances);
            shader.clear();
        });
    }

    @Override
    public void addAttributeVBO(int binding, FloatBuffer buffer, VertexBuffer.Usage usage, Consumer<Integer> setup) {
        this.bind();
        int vbo = this.extraVBOs.computeIfAbsent(binding, v -> glGenBuffers());
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, buffer, usage.id);
        setup.accept(binding);
    }

    @Inject(method = "close", at = @At("HEAD"))
    public void close(CallbackInfo ci) {
        this.extraVBOs.forEach((binding, vbo) -> glDeleteBuffers(vbo));
    }

    @Shadow
    private VertexFormat.Mode mode;
    @Shadow
    private VertexFormat format;
    @Shadow
    private int indexCount;
    @Shadow
    public abstract VertexFormat.IndexType getIndexType();
    @Shadow
    public abstract void bind();
}
