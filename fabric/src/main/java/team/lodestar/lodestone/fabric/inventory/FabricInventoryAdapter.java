package team.lodestar.lodestone.fabric.inventory;

import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.Direction;
import team.lodestar.lodestone.modules.toolkit.blockentity.IInventoryCapabilityProvider;
import team.lodestar.lodestone.modules.toolkit.inventory.ItemInventory;
import team.lodestar.lodestone.modules.toolkit.inventory.InventoryAccess;

public final class FabricInventoryAdapter {
    private FabricInventoryAdapter() {
    }

    public static void register() {
        InventoryAccess.installResolver((level, pos, side) -> {
            Storage<ItemVariant> storage = ItemStorage.SIDED.find(level, pos, side);
            return storage == null ? null : FabricStorageInventory.create(storage);
        });
        ItemStorage.SIDED.registerFallback((world, pos, state, blockEntity, side) -> {
            if (blockEntity instanceof IInventoryCapabilityProvider provider) {
                return getStorage(provider, side);
            }
            return null;
        });
    }

    public static Storage<ItemVariant> getStorage(IInventoryCapabilityProvider provider, Direction side) {
        ItemInventory inventory = provider.getInventory(side);
        return inventory == null ? null : new FabricItemInventoryStorage(inventory);
    }
}
