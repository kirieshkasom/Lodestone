package team.lodestar.lodestone.modules.datagen.smith.itemmodel;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import team.lodestar.lodestone.modules.datagen.providers.item.LodestoneItemModelBuilder;
import team.lodestar.lodestone.modules.datagen.providers.item.LodestoneItemModelSystem;

import java.util.function.Consumer;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public record ItemModelSmithResult(LodestoneItemModelSystem provider, Item item, LodestoneItemModelBuilder builder) {
    public LodestoneItemModelBuilder parentedToThis() {
        return provider.getBuilder(builder.getLocation().getPath() + "_child").parent(builder);
    }

    public LodestoneItemModelBuilder parentedToThis(String childName) {
        return provider.getBuilder(builder.getLocation().getPath() + "_" + childName).parent(builder);
    }

    public LodestoneItemBuilderTransforms addSeparateTransformData() {
        return new LodestoneItemBuilderTransforms(builder);
    }

    public LodestoneItemModelBuilder addModelLayerData(ResourceLocation... textures) {
        builder.parent(new team.lodestar.lodestone.modules.datagen.model.ModelFile.UncheckedModelFile(ResourceLocation.withDefaultNamespace("item/generated")));
        for (int index = 0; index < textures.length; index++) {
            builder.texture("layer" + index, textures[index]);
        }
        return builder;
    }

    public ItemModelSmithResult applyModifier(Consumer<ItemModelSmithResult> modifier) {
        modifier.accept(this);
        return this;
    }

    public static final class LodestoneItemBuilderTransforms {
        private final LodestoneItemModelBuilder builder;

        private LodestoneItemBuilderTransforms(LodestoneItemModelBuilder builder) {
            this.builder = builder;
        }

        public LodestoneItemBuilderTransforms display(String transform, JsonObject value) {
            builder.display(transform, value);
            return this;
        }

        public LodestoneItemModelBuilder end() {
            return builder;
        }
    }
}
