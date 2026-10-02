package team.lodestar.lodestone.events;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import team.lodestar.lodestone.modules.toolkit.inventory.InventoryAccess;
import team.lodestar.lodestone.neoforge.inventory.NeoForgeStorageInventory;
import team.lodestar.lodestone.neoforge.inventory.NeoForgeInventoryAdapter;
import team.lodestar.lodestone.registry.common.LodestoneBlockEntities;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public final class NeoForgeInventoryEvents {
    private NeoForgeInventoryEvents() {
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        NeoForgeInventoryAdapter.register(event, LodestoneBlockEntities.MULTIBLOCK_COMPONENT.get());
        InventoryAccess.installResolver((level, pos, side) -> {
            net.neoforged.neoforge.items.IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, side);
            return handler == null ? null : new NeoForgeStorageInventory(handler);
        });
    }
}
