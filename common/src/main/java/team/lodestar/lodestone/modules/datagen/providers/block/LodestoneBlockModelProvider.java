package team.lodestar.lodestone.modules.datagen.providers.block;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import team.lodestar.lodestone.modules.core.datagen.LodestoneDatagenBlockData;
import team.lodestar.lodestone.modules.datagen.DatagenSystemCommons;
import team.lodestar.lodestone.modules.datagen.IDatagenPathfinder;
import team.lodestar.lodestone.modules.datagen.model.ModelFile;
import team.lodestar.lodestone.modules.datagen.providers.LodestoneJsonDataProvider;
import team.lodestar.lodestone.modules.datagen.providers.ResourceFileHelper;
import team.lodestar.lodestone.modules.toolkit.block.LodestoneBlockProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class LodestoneBlockModelProvider extends LodestoneJsonDataProvider implements IDatagenPathfinder {
    private final String modid;
    private final ResourceFileHelper helper;
    private final Map<ResourceLocation, LodestoneBlockModelBuilder> generatedModels = new HashMap<>();
    private final String folder = "block";

    public LodestoneBlockModelProvider(PackOutput output, String modid) {
        this(output, modid, ResourceFileHelper.empty());
    }

    public LodestoneBlockModelProvider(PackOutput output, String modid, ResourceFileHelper helper) {
        super(output, PackOutput.Target.RESOURCE_PACK, "models", modid + " Block Models");
        this.modid = modid;
        this.helper = helper;
    }

    public LodestoneBlockModelBuilder getBuilder(String path) {
        if (path == null) {
            throw new NullPointerException("Path must not be null");
        }
        ResourceLocation modelPath = appendFolder(path.contains(":") ? ResourceLocation.parse(path) : ResourceLocation.fromNamespaceAndPath(modid, path));
        modelPath = DatagenSystemCommons.modifyModelPath(modelPath);
        helper.trackGenerated(modelPath);
        LodestoneBlockModelBuilder builder = generatedModels.computeIfAbsent(modelPath, location -> new LodestoneBlockModelBuilder(location, helper));
        setRenderType(builder);
        return builder;
    }

    public void clearGeneratedModels() {
        generatedModels.clear();
        generatedJson.clear();
    }

    public void setRenderType(LodestoneBlockModelBuilder builder) {
        Block block = DatagenSystemCommons.CURRENT_BLOCK;
        if (block != null && block.properties() instanceof LodestoneBlockProperties properties) {
            LodestoneDatagenBlockData data = properties.getDatagenData();
            if (data.renderType != null) {
                builder.renderType(data.renderType);
            }
        }
    }

    public ResourceFileHelper helper() {
        return helper;
    }

    @Override
    public String getModId() {
        return modid;
    }

    @Override
    public String getFolder() {
        return folder;
    }

    public ModelFile getExistingFile(ResourceLocation path) {
        return new ModelFile.ExistingModelFile(DatagenSystemCommons.modifyModelParentPath(path), helper);
    }

    public LodestoneBlockModelBuilder withExistingParent(Block block, ResourceLocation parent, String textureName) {
        String name = getBlockName(block);
        return withExistingParent(block, parent, builder -> builder.texture(textureName, getBlockTexture(name)));
    }

    public LodestoneBlockModelBuilder withExistingParent(Block block, ResourceLocation parent, Consumer<LodestoneBlockModelBuilder> appender) {
        LodestoneBlockModelBuilder builder = withExistingParent(getBlockName(block), parent);
        appender.accept(builder);
        return builder;
    }

    public ModelFile predefinedModel(Block block) {
        return predefinedModel(block, "");
    }

    public ModelFile predefinedModel(Block block, String affix) {
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
        return getExistingFile(id.withSuffix(affix));
    }

    public LodestoneBlockModelBuilder withExistingParent(String name, ResourceLocation parent) {
        return getBuilder(name).parent(new ModelFile.ExistingModelFile(parent, helper));
    }

    public LodestoneBlockModelBuilder withExistingParent(String name, ResourceLocation parent, String textureKey, ResourceLocation texture) {
        return withExistingParent(name, parent).texture(textureKey, texture);
    }

    public LodestoneBlockModelBuilder cubeAll(String name, ResourceLocation texture) {
        return withExistingParent(name, ResourceLocation.withDefaultNamespace("block/cube_all")).texture("all", texture);
    }

    public LodestoneBlockModelBuilder cross(String name, ResourceLocation texture) {
        return withExistingParent(name, ResourceLocation.withDefaultNamespace("block/cross")).texture("cross", texture);
    }

    public LodestoneBlockModelBuilder crop(String name, ResourceLocation texture) {
        return withExistingParent(name, ResourceLocation.withDefaultNamespace("block/crop")).texture("crop", texture);
    }

    public LodestoneBlockModelBuilder carpet(String name, ResourceLocation texture) {
        return withExistingParent(name, ResourceLocation.withDefaultNamespace("block/carpet")).texture("wool", texture);
    }

    public LodestoneBlockModelBuilder cubeBottomTop(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        return withExistingParent(name, ResourceLocation.withDefaultNamespace("block/cube_bottom_top"))
                .texture("side", side).texture("bottom", bottom).texture("top", top);
    }

    public LodestoneBlockModelBuilder cubeColumnHorizontal(String name, ResourceLocation side, ResourceLocation end) {
        return withExistingParent(name, ResourceLocation.withDefaultNamespace("block/cube_column_horizontal"))
                .texture("side", side).texture("end", end);
    }

    public LodestoneBlockModelBuilder cubeColumn(String name, ResourceLocation side, ResourceLocation end) {
        return withExistingParent(name, ResourceLocation.withDefaultNamespace("block/cube_column"))
                .texture("side", side).texture("end", end);
    }

    public LodestoneBlockModelBuilder stairs(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        return stairsModel(name, "block/stairs", side, bottom, top);
    }

    public LodestoneBlockModelBuilder stairsInner(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        return stairsModel(name, "block/inner_stairs", side, bottom, top);
    }

    public LodestoneBlockModelBuilder stairsOuter(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        return stairsModel(name, "block/outer_stairs", side, bottom, top);
    }

    private LodestoneBlockModelBuilder stairsModel(String name, String parent, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        return withExistingParent(name, ResourceLocation.withDefaultNamespace(parent)).texture("side", side).texture("bottom", bottom).texture("top", top);
    }

    public LodestoneBlockModelBuilder slab(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        return slabModel(name, "block/slab", side, bottom, top);
    }

    public LodestoneBlockModelBuilder slabTop(String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        return slabModel(name, "block/slab_top", side, bottom, top);
    }

    private LodestoneBlockModelBuilder slabModel(String name, String parent, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        return withExistingParent(name, ResourceLocation.withDefaultNamespace(parent)).texture("side", side).texture("bottom", bottom).texture("top", top);
    }

    public LodestoneBlockModelBuilder button(String name, ResourceLocation texture) {
        return withExistingParent(name, ResourceLocation.withDefaultNamespace("block/button")).texture("texture", texture);
    }

    public LodestoneBlockModelBuilder buttonPressed(String name, ResourceLocation texture) {
        return withExistingParent(name, ResourceLocation.withDefaultNamespace("block/button_pressed")).texture("texture", texture);
    }

    public LodestoneBlockModelBuilder pressurePlate(String name, ResourceLocation texture) {
        return withExistingParent(name, ResourceLocation.withDefaultNamespace("block/pressure_plate_up")).texture("texture", texture);
    }

    public LodestoneBlockModelBuilder pressurePlateDown(String name, ResourceLocation texture) {
        return withExistingParent(name, ResourceLocation.withDefaultNamespace("block/pressure_plate_down")).texture("texture", texture);
    }

    public LodestoneBlockModelBuilder fencePost(String name, ResourceLocation texture) {
        return withExistingParent(name, ResourceLocation.withDefaultNamespace("block/fence_post")).texture("texture", texture);
    }

    public LodestoneBlockModelBuilder fenceSide(String name, ResourceLocation texture) {
        return withExistingParent(name, ResourceLocation.withDefaultNamespace("block/fence_side")).texture("texture", texture);
    }

    public LodestoneBlockModelBuilder wallPost(String name, ResourceLocation texture) {
        return withExistingParent(name, ResourceLocation.withDefaultNamespace("block/template_wall_post")).texture("wall", texture);
    }

    public LodestoneBlockModelBuilder wallSide(String name, ResourceLocation texture) {
        return withExistingParent(name, ResourceLocation.withDefaultNamespace("block/template_wall_side")).texture("wall", texture);
    }

    public LodestoneBlockModelBuilder wallSideTall(String name, ResourceLocation texture) {
        return withExistingParent(name, ResourceLocation.withDefaultNamespace("block/template_wall_side_tall")).texture("wall", texture);
    }

    public LodestoneBlockModelBuilder fenceGate(String name, ResourceLocation texture) {
        return gateModel(name, "block/template_fence_gate", texture);
    }

    public LodestoneBlockModelBuilder fenceGateOpen(String name, ResourceLocation texture) {
        return gateModel(name, "block/template_fence_gate_open", texture);
    }

    public LodestoneBlockModelBuilder fenceGateWall(String name, ResourceLocation texture) {
        return gateModel(name, "block/template_fence_gate_wall", texture);
    }

    public LodestoneBlockModelBuilder fenceGateWallOpen(String name, ResourceLocation texture) {
        return gateModel(name, "block/template_fence_gate_wall_open", texture);
    }

    private LodestoneBlockModelBuilder gateModel(String name, String parent, ResourceLocation texture) {
        return withExistingParent(name, ResourceLocation.withDefaultNamespace(parent)).texture("texture", texture);
    }

    public LodestoneBlockModelBuilder door(String name, String parent, ResourceLocation bottom, ResourceLocation top) {
        return withExistingParent(name, ResourceLocation.withDefaultNamespace(parent)).texture("bottom", bottom).texture("top", top);
    }

    public LodestoneBlockModelBuilder trapdoor(String name, String parent, ResourceLocation texture) {
        return withExistingParent(name, ResourceLocation.withDefaultNamespace(parent)).texture("texture", texture);
    }

    public LodestoneBlockModelBuilder orientableWithBottom(String name, ResourceLocation side, ResourceLocation front, ResourceLocation bottom, ResourceLocation top) {
        return withExistingParent(name, ResourceLocation.withDefaultNamespace("block/orientable_with_bottom"))
                .texture("side", side).texture("front", front).texture("bottom", bottom).texture("top", top);
    }

    public LodestoneBlockModelBuilder torch(String name, ResourceLocation texture) {
        return withExistingParent(name, ResourceLocation.withDefaultNamespace("block/template_torch")).texture("torch", texture);
    }

    public LodestoneBlockModelBuilder torchWall(String name, ResourceLocation texture) {
        return withExistingParent(name, ResourceLocation.withDefaultNamespace("block/template_torch_wall")).texture("torch", texture);
    }

    public LodestoneBlockModelBuilder sign(String name, ResourceLocation texture) {
        return withExistingParent(name, ResourceLocation.withDefaultNamespace("block/template_sign")).texture("particle", texture);
    }

    public LodestoneBlockModelBuilder grassBlockModel(Block block) {
        String name = getBlockName(block);
        return cubeBottomTop(name, getBlockTexture(name), ResourceLocation.withDefaultNamespace("block/dirt"), getBlockTexture(name + "_top"));
    }

    public LodestoneBlockModelBuilder leavesBlockModel(Block block) {
        String name = getBlockName(block);
        return withExistingParent(name, ResourceLocation.withDefaultNamespace("block/leaves"), "all", getBlockTexture(name));
    }

    public LodestoneBlockModelBuilder crossModel(Block block) {
        String name = getBlockName(block);
        return cross(name, getBlockTexture(name));
    }

    public LodestoneBlockModelBuilder cubeBottomTop(Block block) {
        String name = getBlockName(block);
        return cubeBottomTop(name, getBlockTexture(name + "_side"), getBlockTexture(name + "_bottom"), getBlockTexture(name + "_top"));
    }

    public LodestoneBlockModelBuilder orientableWithBottom(Block block) {
        String name = getBlockName(block);
        return orientableWithBottom(name, getBlockTexture(name + "_side"), getBlockTexture(name + "_front"), getBlockTexture(name + "_bottom"), getBlockTexture(name + "_top"));
    }

    public LodestoneBlockModelBuilder airModel(Block block) {
        return withExistingParent(getBlockName(block), ResourceLocation.withDefaultNamespace("block/air"));
    }

    public LodestoneBlockModelBuilder cubeModelAirTexture(Block block) {
        return cubeAll(getBlockName(block), ResourceLocation.withDefaultNamespace("block/air"));
    }

    @Override
    public java.util.concurrent.CompletableFuture<?> run(net.minecraft.data.CachedOutput output) {
        generatedJson.clear();
        generatedModels.forEach((location, builder) -> save(location, builder.toJson()));
        return super.run(output);
    }
}
