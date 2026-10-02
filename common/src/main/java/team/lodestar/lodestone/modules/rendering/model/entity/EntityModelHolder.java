package team.lodestar.lodestone.modules.rendering.model.entity;

import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.*;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.*;
import net.neoforged.neoforge.client.event.*;

import java.util.function.*;

public class EntityModelHolder<T extends Model> {
    private final ModelLayerLocation layer;
    private final Function<ModelPart, T> modelBuilder;
    private final Supplier<LayerDefinition> definitionBuilder;
    private T model;

    public EntityModelHolder(ResourceLocation model, Function<ModelPart, T> modelBuilder, Supplier<LayerDefinition> definitionBuilder) {
        this(new ModelLayerLocation(model, "main"), modelBuilder, definitionBuilder);
    }

    public EntityModelHolder(ModelLayerLocation layer, Function<ModelPart, T> modelBuilder, Supplier<LayerDefinition> definitionBuilder) {
        this.layer = layer;
        this.modelBuilder = modelBuilder;
        this.definitionBuilder = definitionBuilder;
    }

    public void bake(EntityRenderersEvent.AddLayers event) {
        model = modelBuilder.apply(event.getEntityModels().bakeLayer(layer));
    }

    public void register(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(layer, definitionBuilder);
    }

    public T getModel() {
        return model;
    }
}