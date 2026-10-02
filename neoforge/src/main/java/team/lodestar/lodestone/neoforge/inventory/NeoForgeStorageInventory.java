package team.lodestar.lodestone.neoforge.inventory;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import team.lodestar.lodestone.modules.toolkit.inventory.ItemInventory;

public final class NeoForgeStorageInventory implements ItemInventory {
    private final IItemHandler handler;

    public NeoForgeStorageInventory(IItemHandler handler) {
        this.handler = handler;
    }

    @Override
    public int getSlots() {
        return handler.getSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return handler.getStackInSlot(slot);
    }

    @Override
    public int getSlotLimit(int slot) {
        return handler.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return handler.isItemValid(slot, stack);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return handler.insertItem(slot, stack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return handler.extractItem(slot, amount, simulate);
    }
}
