package team.lodestar.lodestone.modules.datagen.providers.item;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import team.lodestar.lodestone.modules.datagen.DatagenSystemCommons;

@SuppressWarnings("NullableProblems")
public class LodestoneItemModelBuilder extends ItemModelBuilder {

    public final LodestoneItemModelSystem provider;

    public LodestoneItemModelBuilder(LodestoneItemModelSystem provider, ResourceLocation outputLocation, ExistingFileHelper existingFileHelper) {
        super(outputLocation, existingFileHelper);
        this.provider = provider;
    }

    @Override
    public ItemModelBuilder texture(String key, ResourceLocation path) {
        var modified = DatagenSystemCommons.modifyTexturePath(path);
        return super.texture(key, modified);
    }

    @Override
    public ItemModelBuilder parent(ModelFile parent) {
        var location = parent.getLocation();
        var modified = DatagenSystemCommons.modifyModelParentPath(location);
        return super.parent(new UncheckedModelFile(modified));

    }
}
