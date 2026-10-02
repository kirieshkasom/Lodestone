package team.lodestar.lodestone.modules.datagen.implementation;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import team.lodestar.lodestone.internal.LodestoneCommon;

import java.util.concurrent.CompletableFuture;

public class LodestoneBlockTagDatagen extends BlockTagsProvider {
    public LodestoneBlockTagDatagen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, LodestoneCommon.LODESTONE, existingFileHelper);
    }

    @Override
    public String getName() {
        return "Lodestone Block Tags";
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {

    }
}