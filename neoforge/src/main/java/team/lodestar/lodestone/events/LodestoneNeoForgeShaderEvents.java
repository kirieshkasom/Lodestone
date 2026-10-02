package team.lodestar.lodestone.events;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import team.lodestar.lodestone.internal.LodestoneCommon;
import team.lodestar.lodestone.registry.client.LodestoneShaders;
import team.lodestar.lodestone.systems.rendering.shader.ShaderRegistrar;

@EventBusSubscriber(value = Dist.CLIENT, modid = LodestoneCommon.LODESTONE, bus = EventBusSubscriber.Bus.MOD)
public final class LodestoneNeoForgeShaderEvents {
    private LodestoneNeoForgeShaderEvents() {
    }

    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) {
        ShaderRegistrar registrar = new ShaderRegistrar(event.getResourceProvider(), event::registerShader);
        LodestoneShaders.shaderRegistry(registrar);
    }
}
