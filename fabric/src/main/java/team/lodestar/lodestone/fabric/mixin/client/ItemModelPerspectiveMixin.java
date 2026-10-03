package team.lodestar.lodestone.fabric.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import team.lodestar.lodestone.fabric.rendering.FabricPerspectiveModel;

@Mixin(ItemRenderer.class)
public class ItemModelPerspectiveMixin {
    @ModifyVariable(method = "render", at = @At("HEAD"), argsOnly = true)
    private BakedModel lodestone$selectPerspective(BakedModel model, @Local(argsOnly = true) ItemDisplayContext context) {
        return model instanceof FabricPerspectiveModel perspectives ? perspectives.lodestone$perspective(context) : model;
    }
}
