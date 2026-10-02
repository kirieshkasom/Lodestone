package team.lodestar.lodestone.test;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import team.lodestar.lodestone.fabric.rendering.FabricLodestoneArmorRenderer;
import team.lodestar.lodestone.modules.rendering.model.entity.armor.LodestoneArmorClientItemExtensions;
import team.lodestar.lodestone.registry.client.LodestoneWorldEventRenderers;
import team.lodestar.lodestone.systems.rendering.shader.ExtendedShaderInstance;

import java.io.IOException;

public final class FabricTestClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        TestClient.install();
        ClientTickEvents.END_CLIENT_TICK.register(TestGpuSmoke::tick);
        TestClient.ARMOR.register((layer, definition) -> EntityModelLayerRegistry.registerModelLayer(layer, definition::get));
        FabricLodestoneArmorRenderer.register(new LodestoneArmorClientItemExtensions(TestClient.ARMOR), TestContent.HELMET.get());
        LodestoneWorldEventRenderers.registerRenderer(TestContent.EVENT.get(), new TestWorldEventRenderer());
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, access) -> TestClient.register(dispatcher, (source, component) -> source.sendFeedback(component)));
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return TestContent.id("pooled_shader");
            }

            @Override
            public void onResourceManagerReload(ResourceManager resources) {
                try {
                    ExtendedShaderInstance previous = TestClient.POOLED_SHADER.getShaderInstance();
                    TestClient.POOLED_SHADER.setShaderInstance(TestClient.POOLED_SHADER.createInstance(resources));
                    if (previous != null) {
                        previous.close();
                    }
                } catch (IOException exception) {
                    throw new IllegalStateException("Could not load the test pooled shader", exception);
                }
            }
        });
    }
}
