package team.lodestar.lodestone.systems.rendering.shader;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.preprocessor.GlslPreprocessor;
import com.mojang.blaze3d.shaders.Shader;
import com.mojang.blaze3d.systems.RenderSystem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.lwjgl.opengl.GL32.*;

@OnlyIn(Dist.CLIENT)
public class LodestoneProgram {
    private static final int MAX_LOG_LENGTH = 32768;
    private final Type type;
    private final String name;
    private int id;

    private LodestoneProgram(Type type, int id, String name) {
        this.type = type;
        this.id = id;
        this.name = name;
    }

    public void attachToShader(Shader shader) {
        RenderSystem.assertOnRenderThread();
        GlStateManager.glAttachShader(shader.getId(), this.getId());
    }

    public void close() {
        if (this.id != -1) {
            RenderSystem.assertOnRenderThread();
            GlStateManager.glDeleteShader(this.id);
            this.id = -1;
            this.type.getPrograms().remove(this.name);
        }

    }

    public String getName() {
        return this.name;
    }

    public static LodestoneProgram compileShader(Type type, String name, InputStream shaderData, String sourceName, GlslPreprocessor preprocessor) throws IOException {
        RenderSystem.assertOnRenderThread();
        int i = compileShaderInternal(type, name, shaderData, sourceName, preprocessor);
        LodestoneProgram program = new LodestoneProgram(type, i, name);
        type.getPrograms().put(name, program);
        return program;
    }

    protected static int compileShaderInternal(Type type, String name, InputStream shaderData, String sourceName, GlslPreprocessor preprocessor) throws IOException {
        String s = IOUtils.toString(shaderData, StandardCharsets.UTF_8);
        if (s == null) {
            throw new IOException("Could not load program " + type.getName());
        } else {
            int i = GlStateManager.glCreateShader(type.getGlType());
            GlStateManager.glShaderSource(i, preprocessor.process(s));
            GlStateManager.glCompileShader(i);
            if (GlStateManager.glGetShaderi(i, 35713) == 0) {
                String s1 = StringUtils.trim(GlStateManager.glGetShaderInfoLog(i, 32768));
                throw new IOException("Couldn't compile " + type.getName() + " program (" + sourceName + ", " + name + ") : " + s1);
            } else {
                return i;
            }
        }
    }

    protected int getId() {
        return this.id;
    }

    @OnlyIn(Dist.CLIENT)
    public enum Type {
        VERTEX("vertex", ".vsh", GL_VERTEX_SHADER),
        FRAGMENT("fragment", ".fsh", GL_FRAGMENT_SHADER),
        GEOMETRY("geometry", ".gsh", GL_GEOMETRY_SHADER);

        private final String name;
        private final String extension;
        private final int glType;
        private final Map<String, LodestoneProgram> programs = Maps.newHashMap();

        Type(String name, String extension, int glType) {
            this.name = name;
            this.extension = extension;
            this.glType = glType;
        }

        public String getName() {
            return this.name;
        }

        public String getExtension() {
            return this.extension;
        }

        int getGlType() {
            return this.glType;
        }

        public Map<String, LodestoneProgram> getPrograms() {
            return this.programs;
        }
    }
}
