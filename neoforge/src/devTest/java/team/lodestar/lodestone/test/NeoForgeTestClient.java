package team.lodestar.lodestone.test;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import team.lodestar.lodestone.registry.client.LodestoneWorldEventRenderers;
import team.lodestar.lodestone.systems.rendering.shader.ShaderRegistrar;

@EventBusSubscriber(modid = TestContent.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class NeoForgeTestClient {
    @SubscribeEvent
    public static void layers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        TestClient.ARMOR.register(event::registerLayerDefinition);
    }

    @SubscribeEvent
    public static void shaders(RegisterShadersEvent event) {
        TestClient.POOLED_SHADER.register(new ShaderRegistrar(event.getResourceProvider(), event::registerShader));
    }

    @SubscribeEvent
    public static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            TestClient.install();
            LodestoneWorldEventRenderers.registerRenderer(TestContent.EVENT.get(), new TestWorldEventRenderer());
        });
    }
}
