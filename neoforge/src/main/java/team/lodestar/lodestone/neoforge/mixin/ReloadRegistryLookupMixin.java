package team.lodestar.lodestone.neoforge.mixin;

import net.minecraft.core.HolderLookup;
import net.neoforged.neoforge.resource.ContextAwareReloadListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ContextAwareReloadListener.class)
public interface ReloadRegistryLookupMixin {
    @Invoker("getRegistryLookup")
    HolderLookup.Provider lodestone$registryLookup();
}
