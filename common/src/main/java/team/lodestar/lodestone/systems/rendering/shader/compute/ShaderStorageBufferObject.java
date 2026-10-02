package team.lodestar.lodestone.systems.rendering.shader.compute;

import org.lwjgl.system.MemoryUtil;
import team.lodestar.lodestone.systems.rendering.IBufferObject;
import team.lodestar.lodestone.systems.rendering.LodestoneRenderSystem;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL43.*;

/**
 * Shader Storage Buffer Object
 */
public class ShaderStorageBufferObject implements IBufferObject {
    private int bufferId;
    private final Usage usage;
    private final int bindingIndex;
    private FloatBuffer floatBuffer;


    public ShaderStorageBufferObject(Usage usage, int bindingIndex) {
        this(usage, bindingIndex, glGenBuffers());
    }

    public ShaderStorageBufferObject(Usage usage, int bindingIndex, int bufferId) {
        this.usage = usage;
        this.bindingIndex = bindingIndex;
        this.bufferId = bufferId;
        LodestoneRenderSystem.glBindBuffer(GL_SHADER_STORAGE_BUFFER, this.bufferId);
        LodestoneRenderSystem.bindBufferBase(GL_SHADER_STORAGE_BUFFER, bindingIndex, this.bufferId);
        this.registerBufferObject();
    }

    public void upload(FloatBuffer floatBuffer) {
        int size = floatBuffer.remaining();
        if(floatBuffer.remaining() > SystemDetails.getMaxShaderStorageBlockSize())
            throw new RuntimeException("Buffer size exceeds maximum shader storage block size");
        LodestoneRenderSystem.glBindBuffer(GL_SHADER_STORAGE_BUFFER, this.bufferId);
        glBufferData(GL_SHADER_STORAGE_BUFFER, floatBuffer, this.usage.getGlEnum());
        LodestoneRenderSystem.glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
        this.floatBuffer = floatBuffer;
    }

    public static void unbind() {
        LodestoneRenderSystem.glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
    }

    public int getId() {
        return this.bufferId;
    }

    public int getBindingIndex() {
        return this.bindingIndex;
    }

    public Usage getUsage() {
        return this.usage;
    }

    public FloatBuffer getFloatBuffer() {
        return this.floatBuffer;
    }

    @Override
    public String toString() {
        return "ShaderStorageBufferObject{" +
                "bufferId=" + bufferId +
                ", usage=" + usage +
                ", bindingIndex=" + bindingIndex +
                '}';
    }

    @Override
    public void destroy() {
        if (this.bufferId != 0)
            glDeleteProgram(this.bufferId);

        if (this.floatBuffer != null)
            MemoryUtil.memFree(this.floatBuffer);
    }

    public enum Usage {
        STREAM_DRAW(GL_STREAM_DRAW),
        STREAM_READ(GL_STREAM_READ),
        STREAM_COPY(GL_STREAM_COPY),
        STATIC_DRAW(GL_STATIC_DRAW),
        STATIC_READ(GL_STATIC_READ),
        STATIC_COPY(GL_STATIC_COPY),
        DYNAMIC_DRAW(GL_DYNAMIC_DRAW),
        DYNAMIC_READ(GL_DYNAMIC_READ),
        DYNAMIC_COPY(GL_DYNAMIC_COPY);

        private final int glEnum;

        Usage(int glEnum) {
            this.glEnum = glEnum;
        }

        public int getGlEnum() {
            return glEnum;
        }
    }
}
