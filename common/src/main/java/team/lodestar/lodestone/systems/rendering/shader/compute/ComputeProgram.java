package team.lodestar.lodestone.systems.rendering.shader.compute;

import com.mojang.blaze3d.shaders.ProgramManager;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import org.apache.commons.io.IOUtils;
import team.lodestar.lodestone.LodestoneLib;
import team.lodestar.lodestone.systems.rendering.IBufferObject;
import team.lodestar.lodestone.systems.rendering.LodestoneRenderSystem;
import team.lodestar.lodestone.systems.rendering.shader.LodestoneShader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL43.*;

public class ComputeProgram implements IBufferObject, LodestoneShader {
    private int programId;
    private Shader shader;
    private ResourceLocation shaderLocation;
    private Map<String, ShaderStorageBufferObject> ssbos = new HashMap<>();
    private static final int[] maxWorkGroupSize = new int[] {-1, -1, -1};
    private static final int[] maxWorkGroupCount = new int[] {-1, -1, -1};
    private static int maxWorkGroupInvocations = -1;

    public ComputeProgram(ResourceLocation shaderLocation) {
        this.shaderLocation = shaderLocation;
        this.registerBufferObject();
    }

    @Override
    public void register(RegisterShadersEvent event) {
        loadConstraints();
        this.loadShader(event.getResourceProvider());
    }

    private void loadShader(ResourceProvider provider) {
        var version = SystemDetails.getOpenglVersion();
        if (version[0] < 4 || (version[0] == 4 && version[1] < 3)) {
            LodestoneLib.LOGGER.warn("Compute shaders are not supported on this system (OpenGL {}.{})", version[0], version[1]);
            return;
        }
        this.destroy();
        try {
            this.programId = ProgramManager.createProgram();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create compute shader program");
        }
        this.shader = new Shader(provider, this.shaderLocation);
        this.attachShader(this.shader);
        this.link();
    }

    public void bindAndDispatch(int x, int y, int z) {
        this.bind();
        glDispatchCompute(x, y, z);
    }

    public void memoryBarrier(int barrier) {
        glMemoryBarrier(barrier);
    }

    public void bindSSBO(String name, ShaderStorageBufferObject.Usage usage, int bindingIndex) {
        ShaderStorageBufferObject ssbo = this.ssbos.get(name);
        if (ssbo == null) {
            ssbo = new ShaderStorageBufferObject(usage, bindingIndex);
            this.ssbos.put(name, ssbo);
        }
        LodestoneRenderSystem.bindBufferBase(GL_SHADER_STORAGE_BUFFER, bindingIndex, ssbo.getId());
    }

    public void bindSSBO(String name, ShaderStorageBufferObject ssbo, int bindingIndex) {
        this.ssbos.put(name, ssbo);
        LodestoneRenderSystem.bindBufferBase(GL_SHADER_STORAGE_BUFFER, bindingIndex, ssbo.getId());
    }

    private void attachShader(Shader shader) {
        glAttachShader(this.programId, shader.getId());
    }

    private void detachShader(Shader shader) {
        glDetachShader(this.programId, shader.getId());
    }

    private void link() {
        glLinkProgram(this.programId);
    }
    public void bind() {
        ProgramManager.glUseProgram(this.programId);
    }

    public static void loadConstraints() {
        for (int i = 0; i < 3; i++) {
            if (maxWorkGroupSize[i] == -1)
                maxWorkGroupSize[i] = glGetIntegeri(GL_MAX_COMPUTE_WORK_GROUP_SIZE, i);
            if (maxWorkGroupCount[i] == -1)
                maxWorkGroupCount[i] = glGetIntegeri(GL_MAX_COMPUTE_WORK_GROUP_COUNT, i);
        }
        if (maxWorkGroupInvocations == -1)
            maxWorkGroupInvocations = glGetInteger(GL_MAX_COMPUTE_WORK_GROUP_INVOCATIONS);
    }

    public int queryLinearWorkGroupSize() {
        int[] size = new int[3];
        glGetProgramiv(this.programId, GL_COMPUTE_WORK_GROUP_SIZE, size);
        return size[0] * size[1] * size[2];
    }

    public Shader getShader() {
        return shader;
    }

    public void reload() {
        this.loadShader(Minecraft.getInstance().getResourceManager());
    }

    @Override
    public void destroy() {
        if (this.programId != 0)
            glDeleteProgram(this.programId);
        if (this.shader != null)
            this.shader.destroy();
        this.programId = 0;
        this.shader = null;
    }

    public static class Shader implements IBufferObject {
        private ResourceLocation shaderLocation;
        private int shaderId;
        private String source;
        private int[] localSize;

        public Shader(ResourceProvider provider, ResourceLocation shaderLocation) {
            this.shaderLocation = shaderLocation;
            this.openShader(provider, shaderLocation);
            this.preProcess();
            this.compile();
        }

        private void openShader(ResourceProvider provider, ResourceLocation shaderId) {
            ResourceLocation shaderLocation = shaderId.withPrefix("shaders/compute/").withSuffix(".comp");
            try (InputStream stream = provider.getResourceOrThrow(shaderLocation).open()) {
                this.source = IOUtils.toString(stream, StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to open resource: " + shaderLocation);
            }
        }

        public void preProcess() {
            this.localSize = ComputePreprocessor.INSTANCE.getLocalSize(this.source);
            this.verifySize();
            StringBuilder builder = new StringBuilder();
            ComputePreprocessor.INSTANCE.process(this.source).forEach(builder::append);
            this.source = builder.toString();
        }

        public void compile() {
            this.shaderId = glCreateShader(GL_COMPUTE_SHADER);
            glShaderSource(this.shaderId, this.source);
            glCompileShader(this.shaderId);
            if (glGetShaderi(this.shaderId, GL_COMPILE_STATUS) == GL_FALSE) {
                throw new RuntimeException("Failed to compile shader: " + glGetShaderInfoLog(this.shaderId, 1024));
            }
        }

        public void reload(ResourceProvider provider) {
            this.destroy();
            this.openShader(provider, this.shaderLocation);
            this.preProcess();
            this.compile();
        }

        private void verifySize() {
            if (this.localSize[0] * this.localSize[1] * this.localSize[2] > maxWorkGroupInvocations) {
                throw new RuntimeException("Local size exceeds max work group invocations of " + maxWorkGroupInvocations);
            }
        }

        public ResourceLocation getShaderLocation() {
            return this.shaderLocation;
        }

        public String getSource() {
            return this.source;
        }

        public int getId() {
            return this.shaderId;
        }

        public int[] getLocalSize() {
            return this.localSize;
        }

        @Override
        public void destroy() {
            if (this.shaderId != 0)
                glDeleteShader(this.shaderId);
            this.shaderId = 0;
        }
    }
}
