package team.lodestar.lodestone.modules.datagen;

import net.minecraft.resources.ResourceLocation;
import team.lodestar.lodestone.modules.datagen.model.ModelFile;
import team.lodestar.lodestone.modules.datagen.smith.itemmodel.EmptyItemModelSmith;
import team.lodestar.lodestone.modules.datagen.smith.itemmodel.ItemModelSmith;

public class ItemModelSmithTypes {

    public static final ResourceLocation GENERATED = ResourceLocation.parse("item/generated");
    public static final ResourceLocation HANDHELD = ResourceLocation.parse("item/handheld");
    public static final ResourceLocation BUILTIN_ENTITY = ResourceLocation.parse("builtin/entity");

    public static ItemModelSmith GENERATED_ITEM = ItemModelSmith.parentedItem(GENERATED, true);
    public static ItemModelSmith HANDHELD_ITEM = ItemModelSmith.parentedItem(HANDHELD, true);
    public static ItemModelSmith BUILTIN_ENTITY_ITEM = ItemModelSmith.parentedItem(BUILTIN_ENTITY, false);

    public static ItemModelSmith NO_DATAGEN = new EmptyItemModelSmith();

    public static ItemModelSmith BLOCK_TEXTURE_ITEM = new ItemModelSmith(((item, provider) -> {
        String name = provider.getItemName(item);
        return provider.createGenericModel(item, GENERATED, provider.getBlockTexture(name));
    }));

    public static ItemModelSmith BLOCK_MODEL_ITEM = new ItemModelSmith(((item, provider) -> {
        String name = provider.getItemName(item);
        return provider.getBuilder(name).parent(new ModelFile.UncheckedModelFile(provider.modLoc("block/" + name)));
    }));

    public static ItemModelSmith BUTTON_ITEM = BLOCK_MODEL_ITEM.addModelParentAffix("_inventory");
    public static ItemModelSmith TRAPDOOR_ITEM = BLOCK_MODEL_ITEM.addModelParentAffix("_bottom");

    public static ItemModelSmith CROSS_MODEL_ITEM = new ItemModelSmith(((item, provider) -> {
        ResourceLocation cross = DatagenSystemCommons.getBlockTextureFromBlockModel("cross");
        return provider.createGenericModel(item, GENERATED, cross);
    }));
    public static ItemModelSmith WALL_ITEM = new ItemModelSmith(((item, provider) -> {
        String name = provider.getItemName(item);
        ResourceLocation wall = DatagenSystemCommons.getBlockTextureFromBlockModel("wall");
        return provider.wallInventory(name, wall);
    }));
    public static ItemModelSmith FENCE_ITEM = new ItemModelSmith(((item, provider) -> {
        String name = provider.getItemName(item);
        ResourceLocation texture = DatagenSystemCommons.getBlockTextureFromBlockModel("texture");
        return provider.fenceInventory(name, texture);
    }));
}
