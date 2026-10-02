package team.lodestar.lodestone.modules.rendering.model.entity;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import team.lodestar.lodestone.internal.client.EntityModelReloadAccess;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class EntityModelHolder<T extends Model> {
    private final ModelLayerLocation layer;
    private final Function<ModelPart, T> modelBuilder;
    private final Supplier<LayerDefinition> definitionBuilder;
    private EntityModelSet bakedModelSet;
    private long bakedGeneration = -1;
    private T model;

    public EntityModelHolder(ResourceLocation model, Function<ModelPart, T> modelBuilder, Supplier<LayerDefinition> definitionBuilder) {
        this(new ModelLayerLocation(model, "main"), modelBuilder, definitionBuilder);
    }

    public EntityModelHolder(ModelLayerLocation layer, Function<ModelPart, T> modelBuilder, Supplier<LayerDefinition> definitionBuilder) {
        this.layer = Objects.requireNonNull(layer);
        this.modelBuilder = Objects.requireNonNull(modelBuilder);
        this.definitionBuilder = Objects.requireNonNull(definitionBuilder);
    }

    /** Bakes this model for an entity model set and records that set for reload-aware access. */
    public T bake(EntityModelSet entityModels) {
        this.model = this.modelBuilder.apply(entityModels.bakeLayer(this.layer));
        this.bakedModelSet = entityModels;
        this.bakedGeneration = reloadGeneration(entityModels);
        return this.model;
    }

    /** Returns the model for this set, baking it again when a resource reload replaces the set. */
    public T getModel(EntityModelSet entityModels) {
        if (this.model == null || this.bakedModelSet != entityModels || this.bakedGeneration != reloadGeneration(entityModels)) {
            return bake(entityModels);
        }
        return this.model;
    }

    private static long reloadGeneration(EntityModelSet entityModels) {
        if (entityModels instanceof EntityModelReloadAccess access) {
            return access.lodestone$getReloadGeneration();
        }
        return 0;
    }

    /** Registers this layer and definition through a loader-provided layer registrar. */
    public void register(BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> layerRegistrar) {
        layerRegistrar.accept(this.layer, this.definitionBuilder);
    }

    /** Returns the model layer used by this holder. */
    public ModelLayerLocation getLayer() {
        return this.layer;
    }

    /** Creates a fresh layer definition for loader registration. */
    public LayerDefinition createLayerDefinition() {
        return this.definitionBuilder.get();
    }

    public T getModel() {
        return this.model;
    }
}
