package team.lodestar.lodestone.modules.datagen.implementation;

import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.util.List;

public final class DataGenerators {
    private DataGenerators() {
    }

    public static List<DataProvider> createProviders(PackOutput output) {
        return List.of(new LodestoneBlockTagDatagen(output), new LodestoneItemTagDatagen(output));
    }
}
