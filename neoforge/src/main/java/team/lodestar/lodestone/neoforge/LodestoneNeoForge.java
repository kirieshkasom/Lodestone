package team.lodestar.lodestone.neoforge;

import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import team.lodestar.lodestone.LodestoneLib;

@Mod("lodestone")
public final class LodestoneNeoForge {
    public LodestoneNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        new LodestoneLib(modEventBus, modContainer);
    }
}
