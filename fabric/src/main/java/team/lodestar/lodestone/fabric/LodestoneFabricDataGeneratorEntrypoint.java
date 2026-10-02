package team.lodestar.lodestone.fabric;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import team.lodestar.lodestone.modules.core.datagen.LodestoneDatagenBlockData;
import team.lodestar.lodestone.modules.datagen.implementation.LodestoneBlockTagDatagen;
import team.lodestar.lodestone.modules.datagen.implementation.LodestoneItemTagDatagen;

public final class LodestoneFabricDataGeneratorEntrypoint implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        LodestoneDatagenBlockData.setDatagenRunning(true);
        FabricDataGenerator.Pack pack = generator.createPack();
        pack.addProvider((FabricDataGenerator.Pack.Factory<LodestoneBlockTagDatagen>) LodestoneBlockTagDatagen::new);
        pack.addProvider((FabricDataGenerator.Pack.Factory<LodestoneItemTagDatagen>) LodestoneItemTagDatagen::new);
    }
}
