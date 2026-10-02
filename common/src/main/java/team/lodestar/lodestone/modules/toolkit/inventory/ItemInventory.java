package team.lodestar.lodestone.modules.toolkit.inventory;

import net.minecraft.world.item.ItemStack;

public interface ItemInventory {
    int getSlots();

    ItemStack getStackInSlot(int slot);

    default void setStackInSlot(int slot, ItemStack stack) {
        throw new UnsupportedOperationException("This inventory does not support direct slot replacement");
    }

    int getSlotLimit(int slot);

    boolean isItemValid(int slot, ItemStack stack);

    ItemStack insertItem(int slot, ItemStack stack, boolean simulate);

    ItemStack extractItem(int slot, int amount, boolean simulate);
}
