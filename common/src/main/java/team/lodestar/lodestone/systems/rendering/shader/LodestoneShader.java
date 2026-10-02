package team.lodestar.lodestone.systems.rendering.shader;

import net.neoforged.neoforge.client.event.RegisterShadersEvent;

public interface LodestoneShader {
    void register(RegisterShadersEvent event);
}
