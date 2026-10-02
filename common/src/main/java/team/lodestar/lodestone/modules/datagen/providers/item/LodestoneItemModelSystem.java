package team.lodestar.lodestone.modules.datagen.providers.item;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import team.lodestar.lodestone.modules.datagen.DatagenSystemCommons;
import team.lodestar.lodestone.modules.datagen.IDatagenPathfinder;
import team.lodestar.lodestone.modules.datagen.model.ModelFile;
import team.lodestar.lodestone.modules.datagen.providers.LodestoneJsonDataProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class LodestoneItemModelSystem extends LodestoneJsonDataProvider implements IDatagenPathfinder {
    public final String modid;
    private final Map<ResourceLocation, LodestoneItemModelBuilder> generatedModels = new HashMap<>();
    private String folder = "item";

    protected LodestoneItemModelSystem(PackOutput output, String modid) {
        super(output, PackOutput.Target.RESOURCE_PACK, "models", modid + " Item Models");
        this.modid = modid;
    }

    @Override
    public String getModId() {
        return modid;
    }

    @Override
    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public ResourceLocation modLoc(String path) {
        return ResourceLocation.fromNamespaceAndPath(modid, path);
    }

    public ModelFile.ExistingModelFile getExistingFile(ResourceLocation path) {
        ResourceLocation modified = DatagenSystemCommons.modifyModelParentPath(path);
        return new ModelFile.ExistingModelFile(modified);
    }

    public LodestoneItemModelBuilder getBuilder(String path) {
        if (path == null) {
            throw new NullPointerException("Path must not be null");
        }
        ResourceLocation modelPath = appendFolder(path.contains(":") ? ResourceLocation.parse(path) : ResourceLocation.fromNamespaceAndPath(modid, path));
        modelPath = DatagenSystemCommons.modifyModelPath(modelPath);
        ResourceLocation finalModelPath = modelPath;
        return generatedModels.computeIfAbsent(finalModelPath, location -> new LodestoneItemModelBuilder(this, location));
    }

    public void setTexturePath(String folder) {
        DatagenSystemCommons.ITEM_TEXTURE.setFolder(folder);
    }

    public LodestoneItemModelBuilder createParentedModel(Item item, ResourceLocation modelParent) {
        return getBuilder(getItemName(item)).parent(new ModelFile.UncheckedModelFile(modelParent));
    }

    public LodestoneItemModelBuilder createGenericModel(Item item, ResourceLocation modelParent, ResourceLocation... textures) {
        LodestoneItemModelBuilder itemModelBuilder = createParentedModel(item, modelParent);
        for (int i = 0; i < textures.length; i++) {
            itemModelBuilder.texture("layer" + i, textures[i]);
        }
        return itemModelBuilder;
    }

    public LodestoneItemModelBuilder wallInventory(String name, ResourceLocation texture) {
        return getBuilder(name).parent(getExistingFile(ResourceLocation.withDefaultNamespace("block/wall_inventory"))).texture("wall", texture);
    }

    public LodestoneItemModelBuilder fenceInventory(String name, ResourceLocation texture) {
        return getBuilder(name).parent(getExistingFile(ResourceLocation.withDefaultNamespace("block/fence_inventory"))).texture("texture", texture);
    }

    protected abstract void registerModels();

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        generatedModels.clear();
        generatedJson.clear();
        registerModels();
        for (Map.Entry<ResourceLocation, LodestoneItemModelBuilder> model : generatedModels.entrySet()) {
            save(model.getKey(), model.getValue().toJson());
        }
        return super.run(output);
    }
}
