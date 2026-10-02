package team.lodestar.lodestone.fabric;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import team.lodestar.lodestone.modules.core.datagen.DatagenContext;

public final class LodestoneFabricDatagenBootstrap implements PreLaunchEntrypoint {
    @Override
    public void onPreLaunch() {
        DatagenContext.setDatagenRunning(System.getProperty("fabric-api.datagen") != null);
    }
}
