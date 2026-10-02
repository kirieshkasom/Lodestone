package team.lodestar.lodestone.neoforge.rendering;

import net.minecraft.client.model.Model;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import team.lodestar.lodestone.modules.rendering.model.entity.EntityModelHolder;

public final class NeoForgeEntityModelLayerRegistrar {
    private NeoForgeEntityModelLayerRegistrar() {
    }

    /** Registers a model holder with the NeoForge layer-definition event. */
    public static <T extends Model> void register(EntityModelHolder<T> holder, EntityRenderersEvent.RegisterLayerDefinitions event) {
        holder.register(event::registerLayerDefinition);
    }

    /** Bakes a model holder using the model set supplied by NeoForge. */
    public static <T extends Model> T bake(EntityModelHolder<T> holder, EntityRenderersEvent.AddLayers event) {
        return holder.bake(event.getEntityModels());
    }
}
