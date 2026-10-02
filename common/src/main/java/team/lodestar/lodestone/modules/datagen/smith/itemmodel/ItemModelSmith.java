package team.lodestar.lodestone.modules.datagen.smith.itemmodel;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import org.jetbrains.annotations.Nullable;
import team.lodestar.lodestone.modules.datagen.providers.item.LodestoneItemModelSystem;
import team.lodestar.lodestone.modules.datagen.smith.itemmodel.data.DatagenItemQuery;
import team.lodestar.lodestone.modules.datagen.smith.itemmodel.data.ItemModelSystemData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * A class responsible for generating item models when used with an ItemModelProvider
 */
public class ItemModelSmith {

    private final ItemModelSupplier modelSupplier;

    public static ItemModelSmith parentedItem(ResourceLocation parent, boolean useTexture) {
        return new ItemModelSmith(((item, provider) -> {
            var name = provider.getItemName(item);
            if (useTexture) {
                return provider.createGenericModel(item, parent, provider.getItemTexture(name));
            }
            return provider.createParentedModel(item, parent);
        }));
    }

    public ItemModelSmith(ItemModelSupplier modelSupplier) {
        this.modelSupplier = modelSupplier;
    }

    public ConfiguredItemModelSmith modifyResult(Consumer<ItemModelSmithResult> modifier) {
        return configure().modifyResult(modifier);
    }

    public ConfiguredItemModelSmith addModelParentAffix(String affix) {
        return configure().addModelParentAffix(affix);
    }

    public ConfiguredItemModelSmith modifyModelParent(UnaryOperator<String> modelParentModifier) {
        return configure().modifyModelParent(modelParentModifier);
    }

    public ConfiguredItemModelSmith addTextureNameAffix(String affix) {
        return configure().addTextureNameAffix(affix);
    }

    public ConfiguredItemModelSmith modifyTexturePath(UnaryOperator<String> textureNameModifier) {
        return configure().modifyTexturePath(textureNameModifier);
    }

    public ConfiguredItemModelSmith addModelPathAffix(String affix) {
        return configure().addModelPathAffix(affix);
    }

    public ConfiguredItemModelSmith modifyModelPath(UnaryOperator<String> modelPathModifier) {
        return configure().modifyModelPath(modelPathModifier);
    }

    protected ConfiguredItemModelSmith configure() {
        return new ConfiguredItemModelSmith(modelSupplier);
    }

    public List<ItemModelSmithResult> act(ItemModelSystemData data, ItemLike... items) {
        return act(data, DatagenItemQuery.fromList(items));
    }

    public List<ItemModelSmithResult> act(ItemModelSystemData data, Class<? extends Item> itemClass) {
        return act(data, d -> d.ofClass(itemClass));
    }

    public List<ItemModelSmithResult> act(ItemModelSystemData data, Function<ItemModelSystemData, DatagenItemQuery> queried) {
        return act(data, queried.apply(data));
    }

    public List<ItemModelSmithResult> act(ItemModelSystemData data, DatagenItemQuery queried) {
        var result = new ArrayList<ItemModelSmithResult>();
        for (Item item : queried.getItems()) {
            var entry = act(data, item);
            if (entry.isEmpty()) {
                continue;
            }
            result.add(entry.get());
        }
        return result;
    }

    public final Optional<ItemModelSmithResult> act(ItemModelSystemData data, Item item) {
        return data.approveAct(this, item);
    }

    public final ItemModelSmithResult act(LodestoneItemModelSystem provider, Item item) {
        preDatagen(provider, item);
        var model = modelSupplier.act(item, provider);
        var result = new ItemModelSmithResult(provider, item, model);
        postDatagen(result);
        return result;
    }

    protected void preDatagen(LodestoneItemModelSystem provider, Item item) {

    }

    protected void postDatagen(ItemModelSmithResult result) {

    }

    public interface ItemModelSupplier {
        ItemModelBuilder act(Item item, LodestoneItemModelSystem provider);
    }
}