package team.lodestar.lodestone.systems.rendering.shader;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.resources.ResourceLocation;
import team.lodestar.lodestone.internal.LodestoneCommon;

import java.util.ArrayList;
import java.util.List;

public class ShaderRegister {
    public final List<LodestoneShader> shaders = new ArrayList<>();
    public final String modId;

    public ShaderRegister(String modId) {
        this.modId = modId;
    }

    public ShaderHolder register(String id, VertexFormat format) {
        return register(new ShaderHolder(ResourceLocation.fromNamespaceAndPath(modId, id), format));
    }

    public <T extends LodestoneShader> T register(T shader) {
        shaders.add(shader);
        return shader;
    }

    public void init(ShaderRegistrar registrar) {
        LodestoneCommon.LOGGER.info("Registering shaders for mod: {}", modId);
        shaders.forEach(shader -> shader.register(registrar));
    }
}
