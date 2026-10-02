package team.lodestar.lodestone.modules.toolkit.inventory;

import net.minecraft.world.item.*;

public class InventoryItemStackTransaction {

    public static final InventoryItemStackTransaction EMPTY = new InventoryItemStackTransaction(ItemStack.EMPTY, ItemStack.EMPTY, -1);

    private final ItemStack original;
    private final ItemStack updated;
    private final int slot;

    protected InventoryItemStackTransaction(ItemStack original, ItemStack updated, int slot) {
        this.original = original;
        this.updated = updated;
        this.slot = slot;
    }

    public static InventoryItemStackTransaction unchanged(ItemStack stack, int slot) {
        return new InventoryItemStackTransaction(stack, stack, slot);
    }

    public static InventoryItemStackTransaction updated(ItemStack original, ItemStack updated, int slot) {
        return new InventoryItemStackTransaction(original, updated, slot);
    }

    public ItemStack getOriginal() {
        return original;
    }

    public ItemStack getUpdated() {
        return updated;
    }

    public ItemStack getNonEmpty() {
        var original = getOriginal();
        if (original.isEmpty()) {
            return getUpdated();
        }
        return original;
    }

    public int getSlot() {
        return slot;
    }

    public int getExchangedCount(int clamp) {
        int leftover = updated.getCount() - original.getCount();
        return Math.min(leftover, clamp);
    }

    public boolean wasSuccessful() {
        return !original.equals(updated);
    }
}
