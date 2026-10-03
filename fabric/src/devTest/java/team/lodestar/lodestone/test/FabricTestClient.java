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
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.client.resources.model.BakedModel;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemDisplayContext;
import team.lodestar.lodestone.fabric.rendering.FabricPerspectiveModel;
import team.lodestar.lodestone.internal.config.ConfigDefinition;
import team.lodestar.lodestone.modules.core.config.LodestoneConfig;

public final class FabricTestClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        TestClient.install();
        TestGpuSmoke.loaderChecks = FabricTestClient::reviewRegressionChecks;
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

    private static void reviewRegressionChecks() {
        BakedModel model = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getItemModel(TestContent.PERSPECTIVE_PROBE.get());
        if (!(model instanceof FabricPerspectiveModel perspectives)
                || perspectives.lodestone$perspective(ItemDisplayContext.GUI) == perspectives.lodestone$perspective(ItemDisplayContext.GROUND)) {
            throw new AssertionError("Fabric model contexts did not select separate geometry");
        }
        ConfigDefinition<Integer> value = new ConfigDefinition<>(TestContent.MOD_ID, "smoke/value", 1, number -> number > 0);
        new LodestoneConfig(TestContent.MOD_ID, "smoke");
        Path file = FabricLoader.getInstance().getConfigDir().resolve("lodestone").resolve(TestContent.MOD_ID).resolve("smoke.json");
        try {
            Files.writeString(file, "{\"value\": 7}");
            LodestoneConfig.reloadChangedConfigs();
            if (value.get() != 7) {
                throw new AssertionError("Fabric did not reload an externally edited config");
            }
            Files.writeString(file, "{\"value\": -1}");
            LodestoneConfig.reloadChangedConfigs();
            if (value.get() != 1) {
                throw new AssertionError("Fabric hot reload bypassed the validator");
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Could not exercise Fabric config reload", exception);
        }
    }

}
