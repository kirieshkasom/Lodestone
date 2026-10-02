package team.lodestar.lodestone.internal.client;

import com.mojang.blaze3d.preprocessor.GlslPreprocessor;
import com.mojang.blaze3d.shaders.Program;
import net.minecraft.FileUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.apache.commons.io.IOUtils;
import team.lodestar.lodestone.internal.LodestoneCommon;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

public final class ShaderResources {
    private static final String CORE = "shaders/core/";

    private ShaderResources() {
    }

    public static ResourceLocation coreLocation(String path) {
        if (path.startsWith(CORE)) {
            ResourceLocation location = ResourceLocation.parse(path.substring(CORE.length()));
            return location.withPath(value -> CORE + value);
        }
        return ResourceLocation.withDefaultNamespace(path);
    }

    public static ResourceLocation importLocation(String basePath, boolean relative, String path) {
        ResourceLocation location = ResourceLocation.parse(path);
        String normalized = FileUtil.normalizeResourcePath((relative ? basePath : "shaders/include/") + location.getPath());
        return ResourceLocation.fromNamespaceAndPath(location.getNamespace(), normalized);
    }

    public static GlslPreprocessor preprocessor(ResourceProvider resources, String basePath) {
        return new GlslPreprocessor() {
            private final Set<ResourceLocation> imported = new HashSet<>();

            @Override
            public String applyImport(boolean relative, String path) {
                ResourceLocation location = importLocation(basePath, relative, path);
                if (!imported.add(location)) {
                    return null;
                }
                try (Reader reader = resources.openAsReader(location)) {
                    return IOUtils.toString(reader);
                } catch (IOException exception) {
                    LodestoneCommon.LOGGER.error("Could not open GLSL import {}: {}", location, exception.getMessage());
                    return "#error " + exception.getMessage();
                }
            }
        };
    }

    public static Program getOrCreate(ResourceProvider resources, Program.Type type, String name) throws IOException {
        Program cached = type.getPrograms().get(name);
        if (cached != null) {
            return cached;
        }
        ResourceLocation shader = ResourceLocation.parse(name);
        String path = CORE + shader.getPath() + type.getExtension();
        Resource resource = resources.getResourceOrThrow(ResourceLocation.fromNamespaceAndPath(shader.getNamespace(), path));
        try (InputStream stream = resource.open()) {
            return Program.compileShader(type, name, stream, resource.sourcePackId(), preprocessor(resources, FileUtil.getFullResourcePath(path)));
        }
    }
}
