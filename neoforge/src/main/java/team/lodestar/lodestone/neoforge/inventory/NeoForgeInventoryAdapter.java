package team.lodestar.lodestone.neoforge.inventory;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import team.lodestar.lodestone.modules.toolkit.blockentity.IInventoryCapabilityProvider;
import team.lodestar.lodestone.modules.toolkit.inventory.ItemInventory;

public final class NeoForgeInventoryAdapter {
    private NeoForgeInventoryAdapter() {
    }

    public static <T extends BlockEntity> void register(RegisterCapabilitiesEvent event, BlockEntityType<T> type) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, type, (blockEntity, side) -> getItemHandler(blockEntity, side));
    }

    public static NeoForgeItemInventoryAdapter getItemHandler(IInventoryCapabilityProvider provider, Direction side) {
        ItemInventory inventory = provider.getInventory(side);
        return inventory == null ? null : new NeoForgeItemInventoryAdapter(inventory);
    }

    private static NeoForgeItemInventoryAdapter getItemHandler(BlockEntity blockEntity, Direction side) {
        if (blockEntity instanceof IInventoryCapabilityProvider provider) {
            return getItemHandler(provider, side);
        }
        return null;
    }
}
