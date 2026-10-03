package team.lodestar.lodestone.fabric.mixin.gameplay;

import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import team.lodestar.lodestone.fabric.FabricReloadRegistryLookup;

import java.util.List;

@Mixin(value = ReloadableServerResources.class, priority = 900)
public abstract class ReloadRegistryLookupMixin {
    @Inject(method = "listeners", at = @At("RETURN"))
    private void lodestone$registryContext(CallbackInfoReturnable<List<PreparableReloadListener>> callback) {
        ReloadableServerResources resources = (ReloadableServerResources) (Object) this;
        for (PreparableReloadListener listener : callback.getReturnValue()) {
            FabricReloadRegistryLookup.associate(listener, resources.fullRegistries().get());
        }
    }
}
