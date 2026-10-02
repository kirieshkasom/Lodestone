package team.lodestar.lodestone.mixin.client;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.lodestar.lodestone.internal.client.EntityModelReloadAccess;

@Mixin(EntityModelSet.class)
public abstract class EntityModelSetReloadMixin implements EntityModelReloadAccess {
    @Unique
    private long lodestone$reloadGeneration;

    @Override
    public long lodestone$getReloadGeneration() {
        return this.lodestone$reloadGeneration;
    }

    @Inject(method = "onResourceManagerReload", at = @At("TAIL"))
    private void lodestone$advanceReloadGeneration(ResourceManager resourceManager, CallbackInfo ci) {
        this.lodestone$reloadGeneration++;
    }
}
