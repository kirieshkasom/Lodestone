package team.lodestar.lodestone.fabric.rendering;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public final class FabricAdvancedModels {
    private static final Map<ResourceLocation, BlendMode> RENDER_TYPES = new HashMap<>();

    private FabricAdvancedModels() {
    }

    public static void registerRenderType(ResourceLocation id, BlendMode mode) {
        RENDER_TYPES.put(id, mode);
    }

    public static void install() {
        registerRenderType(ResourceLocation.withDefaultNamespace("solid"), BlendMode.SOLID);
        registerRenderType(ResourceLocation.withDefaultNamespace("cutout"), BlendMode.CUTOUT);
        registerRenderType(ResourceLocation.withDefaultNamespace("cutout_mipped"), BlendMode.CUTOUT_MIPPED);
        registerRenderType(ResourceLocation.withDefaultNamespace("translucent"), BlendMode.TRANSLUCENT);
        ModelLoadingPlugin.register(context -> context.modifyModelBeforeBake().register((model, bakeContext) -> {
            if (model instanceof BlockModel block && model instanceof FabricModelData metadata && metadata.lodestone$modelData() != null) {
                AdvancedUnbaked replacement = new AdvancedUnbaked(block, metadata.lodestone$modelData());
                replacement.resolveParents(bakeContext.baker()::getModel);
                return replacement;
            }
            return model;
        }));
    }

    private static BakedModel bakeBlock(BlockModel model, ModelBaker baker, Function<Material, TextureAtlasSprite> sprites, ModelState state) {
        if (model.getRootModel() == ModelBakery.GENERATION_MARKER) {
            return new ItemModelGenerator().generateBlockModel(sprites, model).bake(baker, model, sprites, state, false);
        }
        return model.bake(baker, sprites, state);
    }

    private static final class AdvancedUnbaked implements UnbakedModel {
        private final BlockModel original;
        private final JsonObject json;
        private final Map<ItemDisplayContext, BlockModel> perspectives = new EnumMap<>(ItemDisplayContext.class);
        private final BlockModel base;

        private AdvancedUnbaked(BlockModel original, JsonObject json) {
            this.original = original;
            this.json = json;
            this.base = json.has("base") ? BlockModel.fromString(json.get("base").toString()) : original;
            if (json.has("perspectives")) {
                JsonObject children = json.getAsJsonObject("perspectives");
                for (ItemDisplayContext context : ItemDisplayContext.values()) {
                    if (children.has(context.getSerializedName())) {
                        perspectives.put(context, BlockModel.fromString(children.get(context.getSerializedName()).toString()));
                    }
                }
            }
        }

        @Override
        public Collection<ResourceLocation> getDependencies() {
            List<ResourceLocation> dependencies = new ArrayList<>(original.getDependencies());
            dependencies.addAll(base.getDependencies());
            perspectives.values().forEach(model -> dependencies.addAll(model.getDependencies()));
            return dependencies;
        }

        @Override
        public void resolveParents(Function<ResourceLocation, UnbakedModel> resolver) {
            original.resolveParents(resolver);
            base.resolveParents(resolver);
            perspectives.values().forEach(model -> model.resolveParents(resolver));
        }

        @Override
        public BakedModel bake(ModelBaker baker, Function<Material, TextureAtlasSprite> sprites, ModelState state) {
            if (json.get("loader").getAsString().equals("neoforge:item_layers")) {
                List<BakedModel> layers = new ArrayList<>();
                for (int index = 0; original.hasTexture("layer" + index); index++) {
                    JsonObject layer = json.deepCopy();
                    layer.remove("loader");
                    layer.addProperty("parent", "minecraft:item/generated");
                    JsonObject textures = new JsonObject();
                    textures.addProperty("layer0", original.getMaterial("layer" + index).texture().toString());
                    textures.addProperty("particle", original.getMaterial(original.hasTexture("particle") ? "particle" : "layer0").texture().toString());
                    layer.add("textures", textures);
                    BlockModel model = BlockModel.fromString(layer.toString());
                    model.resolveParents(baker::getModel);
                    layers.add(bakeBlock(model, baker, sprites, state));
                }
                if (layers.isEmpty()) {
                    throw new IllegalStateException("Item layer model has no layer textures: " + original);
                }
                return new LayerModel(layers, json);
            }
            BakedModel baseModel = bakeBlock(base, baker, sprites, state);
            Map<ItemDisplayContext, BakedModel> children = new EnumMap<>(ItemDisplayContext.class);
            perspectives.forEach((context, model) -> children.put(context, bakeBlock(model, baker, sprites, state)));
            return new PerspectiveModel(baseModel, children);
        }
    }

    private static class DelegatingModel implements BakedModel {
        protected final BakedModel base;

        private DelegatingModel(BakedModel base) {
            this.base = base;
        }

        @Override
        public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random) {
            return base.getQuads(state, side, random);
        }

        @Override
        public boolean useAmbientOcclusion() {
            return base.useAmbientOcclusion();
        }

        @Override
        public boolean isGui3d() {
            return base.isGui3d();
        }

        @Override
        public boolean usesBlockLight() {
            return base.usesBlockLight();
        }

        @Override
        public boolean isCustomRenderer() {
            return base.isCustomRenderer();
        }

        @Override
        public TextureAtlasSprite getParticleIcon() {
            return base.getParticleIcon();
        }

        @Override
        public ItemTransforms getTransforms() {
            return base.getTransforms();
        }

        @Override
        public ItemOverrides getOverrides() {
            return base.getOverrides();
        }

    }

    private static final class PerspectiveModel extends DelegatingModel implements FabricPerspectiveModel {
        private final Map<ItemDisplayContext, BakedModel> perspectives;

        private PerspectiveModel(BakedModel base, Map<ItemDisplayContext, BakedModel> perspectives) {
            super(base);
            this.perspectives = Map.copyOf(perspectives);
        }

        @Override
        public BakedModel lodestone$perspective(ItemDisplayContext context) {
            return perspectives.getOrDefault(context, base);
        }
    }

    private static final class LayerModel extends DelegatingModel implements FabricBakedModel {
        private final List<BakedModel> models;
        private final List<LayerData> layers;

        private LayerModel(List<BakedModel> models, JsonObject json) {
            super(models.getFirst());
            this.models = List.copyOf(models);
            this.layers = new ArrayList<>();
            JsonObject data = json.has("neoforge_data") && json.getAsJsonObject("neoforge_data").has("layers") ? json.getAsJsonObject("neoforge_data").getAsJsonObject("layers") : new JsonObject();
            Map<Integer, BlendMode> types = new HashMap<>();
            if (json.has("render_types")) {
                for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject("render_types").entrySet()) {
                    BlendMode mode = RENDER_TYPES.get(ResourceLocation.parse(entry.getKey()));
                    if (mode == null) {
                        throw new IllegalArgumentException("Unregistered Fabric item model render type: " + entry.getKey());
                    }
                    for (JsonElement layer : entry.getValue().getAsJsonArray()) {
                        types.put(layer.getAsInt(), mode);
                    }
                }
            }
            for (int index = 0; index < models.size(); index++) {
                JsonObject layer = data.has(Integer.toString(index)) ? data.getAsJsonObject(Integer.toString(index)) : new JsonObject();
                int color = layer.has("color") ? layer.get("color").getAsInt() : -1;
                int blockLight = layer.has("block_light") ? layer.get("block_light").getAsInt() : 0;
                int skyLight = layer.has("sky_light") ? layer.get("sky_light").getAsInt() : 0;
                RenderMaterial material = RendererAccess.INSTANCE.getRenderer().materialFinder().blendMode(types.getOrDefault(index, BlendMode.TRANSLUCENT)).find();
                layers.add(new LayerData(color, LightTexture.pack(blockLight, skyLight), material));
            }
        }

        @Override
        public boolean isVanillaAdapter() {
            return false;
        }

        @Override
        public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random) {
            List<BakedQuad> quads = new ArrayList<>();
            for (int index = 0; index < models.size(); index++) {
                for (BakedQuad quad : models.get(index).getQuads(state, side, random)) {
                    quads.add(new BakedQuad(quad.getVertices().clone(), index, quad.getDirection(), quad.getSprite(), quad.isShade()));
                }
            }
            return quads;
        }

        @Override
        public void emitItemQuads(ItemStack stack, Supplier<RandomSource> random, RenderContext context) {
            for (int index = 0; index < models.size(); index++) {
                int layerIndex = index;
                LayerData data = layers.get(index);
                context.pushTransform(quad -> {
                    quad.colorIndex(layerIndex);
                    quad.material(data.material());
                    for (int vertex = 0; vertex < 4; vertex++) {
                        int original = quad.color(vertex);
                        int color = multiply(original, data.color());
                        quad.color(vertex, color);
                        int light = quad.lightmap(vertex);
                        quad.lightmap(vertex, LightTexture.pack(Math.max(LightTexture.block(light), LightTexture.block(data.light())), Math.max(LightTexture.sky(light), LightTexture.sky(data.light()))));
                    }
                    return true;
                });
                try {
                    ((FabricBakedModel) models.get(index)).emitItemQuads(stack, random, context);
                } finally {
                    context.popTransform();
                }
            }
        }

        private static int multiply(int first, int second) {
            int result = 0;
            for (int shift = 0; shift < 32; shift += 8) {
                result |= (((first >>> shift) & 255) * ((second >>> shift) & 255) / 255) << shift;
            }
            return result;
        }
    }

    private record LayerData(int color, int light, RenderMaterial material) {
    }
}
