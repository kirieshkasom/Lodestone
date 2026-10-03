package team.lodestar.lodestone.modules.datagen.smith.itemmodel;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import team.lodestar.lodestone.modules.datagen.providers.item.LodestoneItemModelBuilder;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.Set;

public final class LodestoneItemLayerBuilder {
    private final LodestoneItemModelBuilder builder;
    private final Map<Integer, JsonObject> layers = new LinkedHashMap<>();
    private final Map<ResourceLocation, Set<Integer>> renderTypes = new LinkedHashMap<>();
    private final Set<Integer> assignedTypes = new TreeSet<>();

    public LodestoneItemLayerBuilder(LodestoneItemModelBuilder builder) {
        this.builder = builder;
        builder.customLoader(ResourceLocation.fromNamespaceAndPath("neoforge", "item_layers"), this::toJson);
    }

    private void validateLayers(int[] indices) {
        if (indices.length == 0) {
            throw new IllegalArgumentException("At least one layer is required");
        }
        for (int index : indices) {
            if (index < 0) {
                throw new IllegalArgumentException("Layer indices must be nonnegative");
            }
        }
    }

    public LodestoneItemLayerBuilder emissive(int blockLight, int skyLight, int... indices) {
        validateLayers(indices);
        if (blockLight < 0 || blockLight > 15 || skyLight < 0 || skyLight > 15) {
            throw new IllegalArgumentException("Light levels must be between 0 and 15");
        }
        for (int index : indices) {
            JsonObject data = layers.computeIfAbsent(index, ignored -> new JsonObject());
            data.addProperty("block_light", blockLight);
            data.addProperty("sky_light", skyLight);
        }
        return this;
    }

    /** Sets the vertex color multiplier in ARGB format for the selected layers. */
    public LodestoneItemLayerBuilder color(int color, int... indices) {
        validateLayers(indices);
        for (int index : indices) {
            layers.computeIfAbsent(index, ignored -> new JsonObject()).addProperty("color", color);
        }
        return this;
    }

    public LodestoneItemLayerBuilder renderType(String type, int... indices) {
        return renderType(type.contains(":") ? ResourceLocation.parse(type) : ResourceLocation.fromNamespaceAndPath(builder.getLocation().getNamespace(), type), indices);
    }

    public LodestoneItemLayerBuilder renderType(ResourceLocation type, int... indices) {
        validateLayers(indices);
        for (int index : indices) {
            if (assignedTypes.contains(index)) {
                throw new IllegalArgumentException("Render type already assigned to layer " + index);
            }
        }
        Set<Integer> entries = renderTypes.computeIfAbsent(type, ignored -> new TreeSet<>());
        for (int index : indices) {
            assignedTypes.add(index);
            entries.add(index);
        }
        return this;
    }

    private JsonObject toJson(JsonObject json) {
        JsonObject data = new JsonObject();
        JsonObject layerJson = new JsonObject();
        layers.forEach((index, layer) -> layerJson.add(index.toString(), layer.deepCopy()));
        data.add("layers", layerJson);
        json.add("neoforge_data", data);
        JsonObject types = new JsonObject();
        renderTypes.forEach((type, indices) -> {
            JsonArray array = new JsonArray();
            indices.forEach(array::add);
            types.add(type.toString(), array);
        });
        json.add("render_types", types);
        return json;
    }

    public LodestoneItemModelBuilder end() {
        return builder;
    }
}
