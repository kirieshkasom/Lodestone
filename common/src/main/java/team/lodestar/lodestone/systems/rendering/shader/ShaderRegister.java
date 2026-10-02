package team.lodestar.lodestone.systems.rendering.shader;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.resources.*;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import team.lodestar.lodestone.LodestoneLib;

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

    public void init(RegisterShadersEvent event) {
        LodestoneLib.LOGGER.info("Registering shaders for mod: {}", modId);
        shaders.forEach(shader -> shader.register(event));
    }
}
