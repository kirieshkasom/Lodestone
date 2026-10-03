package team.lodestar.lodestone.fabric.rendering;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;

public interface FabricPerspectiveModel {
    BakedModel lodestone$perspective(ItemDisplayContext context);
}
