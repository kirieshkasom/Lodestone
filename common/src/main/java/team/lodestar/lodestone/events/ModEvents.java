package team.lodestar.lodestone.events;

import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.*;
import team.lodestar.lodestone.modules.toolkit.creative_tab.CategorizedCreativeTab;
import team.lodestar.lodestone.registry.common.LodestoneCommandArgumentTypes;
import team.lodestar.lodestone.modules.toolkit.item.*;

@EventBusSubscriber()
public class ModEvents {

    @SubscribeEvent
    public static void registerCommon(FMLCommonSetupEvent event) {
        LodestoneCommandArgumentTypes.registerArgumentTypes();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void buildCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        CategorizedCreativeTab.buildCreativeTabs(event);
        LodestoneItemProperties.buildCreativeTabs(event);
    }
}