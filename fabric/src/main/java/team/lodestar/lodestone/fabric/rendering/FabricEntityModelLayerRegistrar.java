package team.lodestar.lodestone.fabric.rendering;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.model.Model;
import team.lodestar.lodestone.modules.rendering.model.entity.EntityModelHolder;

public final class FabricEntityModelLayerRegistrar {
    private FabricEntityModelLayerRegistrar() {
    }

    /** Registers a model holder with Fabric's entity layer registry. */
    public static <T extends Model> void register(EntityModelHolder<T> holder) {
        EntityModelLayerRegistry.registerModelLayer(holder.getLayer(), holder::createLayerDefinition);
    }
}
