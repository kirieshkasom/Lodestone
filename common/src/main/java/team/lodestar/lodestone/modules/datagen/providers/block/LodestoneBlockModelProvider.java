package team.lodestar.lodestone.modules.datagen.providers.block;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.spongepowered.include.com.google.common.base.Preconditions;
import team.lodestar.lodestone.modules.core.datagen.LodestoneDatagenBlockData;
import team.lodestar.lodestone.modules.datagen.DatagenSystemCommons;
import team.lodestar.lodestone.modules.datagen.IDatagenPathfinder;
import team.lodestar.lodestone.modules.toolkit.block.LodestoneBlockProperties;

import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("NullableProblems")
public final class LodestoneBlockModelProvider extends BlockModelProvider implements IDatagenPathfinder {

    private final Function<ResourceLocation, LodestoneBlockModelBuilder> factory;

    public LodestoneBlockModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, existingFileHelper);
        this.factory = l -> new LodestoneBlockModelBuilder(l, existingFileHelper);
    }

    @Override
    public LodestoneBlockModelBuilder getBuilder(String path) {
        Preconditions.checkNotNull(path, "Path must not be null");
        var modelPath = appendFolder(path.contains(":") ? ResourceLocation.parse(path) : ResourceLocation.fromNamespaceAndPath(modid, path));
        modelPath = DatagenSystemCommons.modifyModelPath(modelPath);
        this.existingFileHelper.trackGenerated(modelPath, MODEL);
        var builder = (LodestoneBlockModelBuilder) generatedModels.computeIfAbsent(modelPath, factory);
        setRenderType(builder);
        return builder;
    }

    public void setRenderType(LodestoneBlockModelBuilder builder) {
        Block currentBlock = DatagenSystemCommons.CURRENT_BLOCK;
        if (currentBlock != null) {
            if (currentBlock.properties() instanceof LodestoneBlockProperties properties) {
                var datagenData = properties.getDatagenData();
                var renderType = datagenData.renderType;
                if (renderType == null) {
                    return;
                }
                builder.renderType(renderType);
            }
        }
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
    protected void registerModels() {

    }

    @Override
    public ModelFile.ExistingModelFile getExistingFile(ResourceLocation path) {
        var modified = DatagenSystemCommons.modifyModelParentPath(path);
        return super.getExistingFile(modified);
    }

    public BlockModelBuilder withExistingParent(Block block, ResourceLocation parent, String textureName) {
        String name = getBlockName(block);
        ResourceLocation texture = getBlockTexture(name);
        return withExistingParent(block, parent, b -> b.texture(textureName, texture));
    }

    public BlockModelBuilder withExistingParent(Block block, ResourceLocation parent, Consumer<BlockModelBuilder> appender) {
        String name = getBlockName(block);
        BlockModelBuilder builder = withExistingParent(name, parent);
        appender.accept(builder);
        return builder;
    }

    public ModelFile predefinedModel(Block block) {
        return predefinedModel(block, "");
    }

    public ModelFile predefinedModel(Block block, String affix) {
        var id = BuiltInRegistries.BLOCK.getKey(block);
        var path = id.withSuffix(affix);
        return getExistingFile(path);
    }

    public ModelFile grassBlockModel(Block block) {
        String name = getBlockName(block);
        ResourceLocation side = getBlockTexture(name);
        ResourceLocation dirt = ResourceLocation.withDefaultNamespace("block/dirt");
        ResourceLocation top = getBlockTexture(name + "_top");
        return cubeBottomTop(name, side, dirt, top);
    }

    public ModelFile leavesBlockModel(Block block) {
        String name = getBlockName(block);
        return withExistingParent(name, ResourceLocation.withDefaultNamespace("block/leaves")).texture("all", getBlockTexture(name));
    }

    public ModelFile crossModel(Block block) {
        String name = getBlockName(block);
        return cross(name, getBlockTexture(name));
    }

    public ModelFile cubeBottomTop(Block block) {
        String name = getBlockName(block);
        ResourceLocation side = getBlockTexture(name + "_side");
        ResourceLocation bottom = getBlockTexture(name + "_bottom");
        ResourceLocation top = getBlockTexture(name + "_top");
        return cubeBottomTop(name, side, bottom, top);
    }

    public ModelFile orientableWithBottom(Block block) {
        String name = getBlockName(block);
        ResourceLocation side = getBlockTexture(name + "_side");
        ResourceLocation front = getBlockTexture(name + "_front");
        ResourceLocation bottom = getBlockTexture(name + "_bottom");
        ResourceLocation top = getBlockTexture(name + "_top");
        return orientableWithBottom(name, side, front, bottom, top);
    }

    public ModelFile airModel(Block block) {
        String name = getBlockName(block);
        return withExistingParent(name, ResourceLocation.withDefaultNamespace("block/air"));
    }

    public ModelFile cubeModelAirTexture(Block block) {
        String name = getBlockName(block);
        return cubeAll(name, ResourceLocation.withDefaultNamespace("block/air"));
    }
}