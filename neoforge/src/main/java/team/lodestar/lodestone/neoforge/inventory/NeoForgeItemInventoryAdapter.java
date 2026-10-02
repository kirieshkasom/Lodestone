package team.lodestar.lodestone.neoforge.inventory;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import team.lodestar.lodestone.modules.toolkit.inventory.ItemInventory;

public final class NeoForgeItemInventoryAdapter implements IItemHandler {
    private final ItemInventory inventory;

    public NeoForgeItemInventoryAdapter(ItemInventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public int getSlots() {
        return inventory.getSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return inventory.getStackInSlot(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return inventory.insertItem(slot, stack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return inventory.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return inventory.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return inventory.isItemValid(slot, stack);
    }
}
