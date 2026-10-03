package team.lodestar.lodestone.modules.datagen.smith.itemmodel;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import team.lodestar.lodestone.modules.datagen.model.ModelFile;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
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

    public LodestoneItemLayerBuilder addModelLayerData(ResourceLocation... textures) {
        for (int index = 0; index < textures.length; index++) {
            builder.texture("layer" + index, textures[index]);
        }
        return new LodestoneItemLayerBuilder(builder);
    }

    public ItemModelSmithResult applyModifier(Consumer<ItemModelSmithResult> modifier) {
        modifier.accept(this);
        return this;
    }

    public static final class LodestoneItemBuilderTransforms {
        private final LodestoneItemModelBuilder builder;
        private ModelFile base;
        private final Map<ItemDisplayContext, ModelFile> perspectives = new LinkedHashMap<>();

        private LodestoneItemBuilderTransforms(LodestoneItemModelBuilder builder) {
            this.builder = builder;
            builder.customLoader(ResourceLocation.fromNamespaceAndPath("neoforge", "separate_transforms"), this::toJson);
        }

        public LodestoneItemBuilderTransforms base(ModelFile model) {
            base = Objects.requireNonNull(model);
            return this;
        }

        public LodestoneItemBuilderTransforms perspective(ItemDisplayContext context, ModelFile model) {
            perspectives.put(Objects.requireNonNull(context), Objects.requireNonNull(model));
            return this;
        }

        private JsonObject modelJson(ModelFile model) {
            JsonObject json = model.toJson();
            if (json.isEmpty()) {
                json.addProperty("parent", model.getLocation().toString());
            }
            return json;
        }

        private JsonObject toJson(JsonObject json) {
            if (base == null) {
                throw new IllegalStateException("Separate transforms require a base model");
            }
            json.add("base", modelJson(base));
            JsonObject children = new JsonObject();
            perspectives.forEach((context, model) -> children.add(context.getSerializedName(), modelJson(model)));
            json.add("perspectives", children);
            return json;
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
