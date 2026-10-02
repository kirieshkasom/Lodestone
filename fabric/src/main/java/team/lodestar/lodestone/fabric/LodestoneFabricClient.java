package team.lodestar.lodestone.fabric;

import net.fabricmc.api.ClientModInitializer;

public final class LodestoneFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        LodestoneFabricClientLifecycle.install();
    }
}
