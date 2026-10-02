package team.lodestar.lodestone.modules.toolkit.inventory;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import team.lodestar.lodestone.modules.toolkit.blockentity.IInventoryCapabilityProvider;

public final class InventoryAccess {
    private static volatile Resolver resolver;

    private InventoryAccess() {
    }

    public static void installResolver(Resolver inventoryResolver) {
        resolver = inventoryResolver;
    }

    public static ItemInventory get(Level level, BlockPos pos, Direction side) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof IInventoryCapabilityProvider provider) {
            return provider.getInventory(side);
        }
        Resolver activeResolver = resolver;
        return activeResolver == null ? null : activeResolver.find(level, pos, side);
    }

    @FunctionalInterface
    public interface Resolver {
        ItemInventory find(Level level, BlockPos pos, Direction side);
    }
}
