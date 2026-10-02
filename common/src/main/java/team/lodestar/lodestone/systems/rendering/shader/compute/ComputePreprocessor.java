package team.lodestar.lodestone.systems.rendering.shader.compute;

import com.mojang.blaze3d.preprocessor.GlslPreprocessor;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;
import team.lodestar.lodestone.LodestoneLib;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;

public class ComputePreprocessor extends GlslPreprocessor {
    private static Pattern concatPattern = Pattern.compile("\\$\\{([^}]+)}");
    private static Pattern localSizePattern = Pattern.compile("(local_size_)([xyz])( *= *)([0-9.]+)");
    public static final ComputePreprocessor INSTANCE = new ComputePreprocessor();

    public int[] getLocalSize(String shaderData) {
        int[] localSize = {-1, -1, -1};
        localSizePattern.matcher(shaderData).results().forEach(matchResult -> {
            int i = matchResult.group(2).charAt(0) - 'x';
            if (i < 0 || i > 2) {
                LodestoneLib.LOGGER.error("Invalid local size direction found: " + matchResult.group(2).charAt(0));
                return;
            }
            localSize[i] = Integer.parseInt(matchResult.group(4));
        });
        return localSize;
    }

    @Override
    public List<String> process(String shaderData) {
//        shaderData = concatPattern.matcher(shaderData).replaceAll(matchResult -> {
//            String group = matchResult.group();
//            LodestoneLib.LOGGER.info(group);
//            return "10";
//        });
        return super.process(shaderData);
    }

    @Nullable
    @Override
    public String applyImport(boolean pUseFullPath, String pDirectory) {
        ResourceLocation resourcelocation = ResourceLocation.parse(pDirectory);
        ResourceLocation resourcelocation1 = ResourceLocation.fromNamespaceAndPath(resourcelocation.getNamespace(), "shaders/include/" + resourcelocation.getPath());
        try {
            Resource resource1 = Minecraft.getInstance().getResourceManager().getResource(resourcelocation1).orElseThrow();

            return IOUtils.toString(resource1.open(), StandardCharsets.UTF_8);
        } catch (IOException ioexception) {
            LodestoneLib.LOGGER.error("Could not open GLSL import {}: {}", pDirectory, ioexception.getMessage());
            return "#error " + ioexception.getMessage();
        }
    }
}
