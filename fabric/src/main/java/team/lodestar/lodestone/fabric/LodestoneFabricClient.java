package team.lodestar.lodestone.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import team.lodestar.lodestone.modules.toolkit.command.worldevent.ParticleDebugCommand;
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
            LiteralCommandNode<FabricClientCommandSource> command = dispatcher.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal("lodec")
                    .then(ParticleDebugCommand.register(FabricClientCommandSource::sendFeedback, FabricClientCommandSource::sendError)));
            dispatcher.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal("lodestonec").redirect(command));
        });
        LodestoneClientReloads.register((id, listener) -> ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new FabricReloadListener(id, listener)));
        LodestoneFabricNetworking.installClient();
        LodestoneFabricClientLifecycle.install();
    }
}
