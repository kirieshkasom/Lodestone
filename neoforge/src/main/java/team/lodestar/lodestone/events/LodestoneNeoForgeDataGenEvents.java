package team.lodestar.lodestone.events;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import team.lodestar.lodestone.internal.LodestoneCommon;
import team.lodestar.lodestone.modules.core.datagen.LodestoneDatagenBlockData;
import team.lodestar.lodestone.modules.datagen.implementation.DataGenerators;

@EventBusSubscriber(modid = LodestoneCommon.LODESTONE, bus = EventBusSubscriber.Bus.MOD)
public final class LodestoneNeoForgeDataGenEvents {
    static {
        LodestoneDatagenBlockData.setDatagenState(net.neoforged.neoforge.data.loading.DatagenModLoader::isRunningDataGen);
    }

    private LodestoneNeoForgeDataGenEvents() {
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        boolean includeServer = event.includeServer();
        for (net.minecraft.data.DataProvider provider : DataGenerators.createProviders(event.getGenerator().getPackOutput())) {
            event.getGenerator().addProvider(includeServer, provider);
        }
    }
}
