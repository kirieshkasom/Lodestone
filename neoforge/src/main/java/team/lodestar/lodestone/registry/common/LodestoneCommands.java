package team.lodestar.lodestone.registry.common;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import team.lodestar.lodestone.internal.LodestoneCommandRegistration;

@EventBusSubscriber
public class LodestoneCommands {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        LodestoneCommandRegistration.registerCommands(event.getDispatcher());
    }
}
