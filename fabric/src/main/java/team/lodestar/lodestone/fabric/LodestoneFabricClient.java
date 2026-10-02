package team.lodestar.lodestone.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import team.lodestar.lodestone.registry.client.LodestoneClientCommands;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;
import team.lodestar.lodestone.internal.client.LodestoneClientReloads;

public final class LodestoneFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        LodestoneFabricRenderEvents.install();
        LodestoneFabricBlockRenderLayers.install();
        FabricParticleProviders.register();
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, access) -> {
            LodestoneClientCommands.registerCommands(dispatcher, (source, component) -> source.sendFeedback(component), (source, component) -> source.sendError(component));
        });
        LodestoneClientReloads.register((id, listener) -> ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new FabricReloadListener(id, listener)));
        LodestoneFabricNetworking.installClient();
        LodestoneFabricClientLifecycle.install();
    }
}
