package team.lodestar.lodestone.modules.datagen.smith.itemmodel;

import net.minecraft.world.item.Item;
import team.lodestar.lodestone.modules.datagen.DatagenSystemCommons;
import team.lodestar.lodestone.modules.datagen.providers.item.LodestoneItemModelSystem;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * A configured instance of an ItemModelSmith
 */
public class ConfiguredItemModelSmith extends ItemModelSmith {

    private Consumer<ItemModelSmithResult> resultModifier;
    private UnaryOperator<String> texturePathModifier;
    private UnaryOperator<String> modelParentModifier;
    private UnaryOperator<String> modelPathModifier;

    public ConfiguredItemModelSmith(ItemModelSmith.ItemModelSupplier modelSupplier) {
        super(modelSupplier);
    }

    @Override
    public ConfiguredItemModelSmith modifyResult(Consumer<ItemModelSmithResult> modifier) {
        this.resultModifier = modifier;
        return this;
    }

    @Override
    public ConfiguredItemModelSmith addModelParentAffix(String affix) {
        return modifyModelParent(s -> s + affix);
    }

    @Override
    public ConfiguredItemModelSmith modifyModelParent(UnaryOperator<String> modelParentModifier) {
        this.modelParentModifier = modelParentModifier;
        return this;
    }

    @Override
    public ConfiguredItemModelSmith addTextureNameAffix(String affix) {
        return modifyTexturePath(s -> s + affix);
    }

    @Override
    public ConfiguredItemModelSmith modifyTexturePath(UnaryOperator<String> textureNameModifier) {
        this.texturePathModifier = textureNameModifier;
        return this;
    }

    @Override
    public ConfiguredItemModelSmith addModelPathAffix(String affix) {
        return modifyModelPath(s -> s + affix);
    }

    @Override
    public ConfiguredItemModelSmith modifyModelPath(UnaryOperator<String> modelPathModifier) {
        this.modelPathModifier = modelPathModifier;
        return this;
    }

    @Override
    protected void preDatagen(LodestoneItemModelSystem provider, Item item) {
        DatagenSystemCommons.MODEL_PARENT.modify(modelParentModifier);

        DatagenSystemCommons.ITEM_MODEL.modify(modelPathModifier);
        DatagenSystemCommons.BLOCK_MODEL.modify(modelPathModifier);
        DatagenSystemCommons.ITEM_TEXTURE.modify(texturePathModifier);
        DatagenSystemCommons.BLOCK_TEXTURE.modify(texturePathModifier);
    }

    @Override
    protected void postDatagen(ItemModelSmithResult result) {
        if (resultModifier != null) {
            result.applyModifier(resultModifier);
        }
        DatagenSystemCommons.MODEL_PARENT.clearModifier();

        DatagenSystemCommons.ITEM_MODEL.clearModifier();
        DatagenSystemCommons.BLOCK_MODEL.clearModifier();
        DatagenSystemCommons.ITEM_TEXTURE.clearModifier();
        DatagenSystemCommons.BLOCK_TEXTURE.clearModifier();
    }
}