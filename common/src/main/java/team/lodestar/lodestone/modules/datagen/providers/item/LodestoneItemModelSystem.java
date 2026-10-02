package team.lodestar.lodestone.modules.datagen.providers.item;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.spongepowered.include.com.google.common.base.Preconditions;
import team.lodestar.lodestone.modules.datagen.DatagenSystemCommons;
import team.lodestar.lodestone.modules.datagen.IDatagenPathfinder;

import java.util.function.Function;

@SuppressWarnings({"NullableProblems", "unused"})
public abstract class LodestoneItemModelSystem extends ItemModelProvider implements IDatagenPathfinder {

    private final Function<ResourceLocation, LodestoneItemModelBuilder> factory;

    public LodestoneItemModelSystem(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, existingFileHelper);
        this.factory = l -> new LodestoneItemModelBuilder(this, l, existingFileHelper);
    }

    @Override
    public String getModId() {
        return modid;
    }

    @Override
    public String getFolder() {
        return folder;
    }

    @Override
    public ModelFile.ExistingModelFile getExistingFile(ResourceLocation path) {
        var modified = DatagenSystemCommons.modifyModelParentPath(path);
        return super.getExistingFile(modified);
    }

    @Override
    public LodestoneItemModelBuilder getBuilder(String path) {
        Preconditions.checkNotNull(path, "Path must not be null");
        var modelPath = appendFolder(path.contains(":") ? ResourceLocation.parse(path) : ResourceLocation.fromNamespaceAndPath(modid, path));
        modelPath = DatagenSystemCommons.modifyModelPath(modelPath);
        this.existingFileHelper.trackGenerated(modelPath, MODEL);
        return (LodestoneItemModelBuilder) generatedModels.computeIfAbsent(modelPath, factory);
    }

    public void setTexturePath(String folder) {
        DatagenSystemCommons.ITEM_TEXTURE.setFolder(folder);
    }

    public ItemModelBuilder createParentedModel(Item item, ResourceLocation modelParent) {
        return getBuilder(getItemName(item)).parent(new ModelFile.UncheckedModelFile(modelParent));
    }

    public ItemModelBuilder createGenericModel(Item item, ResourceLocation modelParent, ResourceLocation... textures) {
        var itemModelBuilder = createParentedModel(item, modelParent);
        for (int i = 0; i < textures.length; i++) {
            itemModelBuilder.texture("layer" + i, textures[i]);
        }
        return itemModelBuilder;
    }
}