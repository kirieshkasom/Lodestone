package team.lodestar.lodestone.fabric;

import net.fabricmc.api.ModInitializer;
import team.lodestar.lodestone.internal.LodestoneCommon;

public final class LodestoneFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        LodestoneCommon.init(new FabricParticleRegistrar());
    }
}
