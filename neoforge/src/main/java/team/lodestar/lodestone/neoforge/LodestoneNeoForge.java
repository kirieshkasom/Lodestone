package team.lodestar.lodestone.neoforge;

import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import team.lodestar.lodestone.LodestoneLib;
import team.lodestar.lodestone.modules.toolkit.reload_listener.ReloadRegistryLookup;
import team.lodestar.lodestone.neoforge.mixin.ReloadRegistryLookupMixin;

@Mod("lodestone")
public final class LodestoneNeoForge {
    public LodestoneNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        ReloadRegistryLookup.install(listener -> ((ReloadRegistryLookupMixin) listener).lodestone$registryLookup());
        new LodestoneLib(modEventBus, modContainer);
    }
}
