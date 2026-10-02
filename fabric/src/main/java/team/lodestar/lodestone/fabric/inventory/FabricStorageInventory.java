package team.lodestar.lodestone.fabric.inventory;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.item.ItemStack;
import team.lodestar.lodestone.modules.toolkit.inventory.ItemInventory;

import java.util.ArrayList;
import java.util.List;

public final class FabricStorageInventory implements ItemInventory {
    private final SlottedStorage<ItemVariant> slottedStorage;
    private final List<StorageView<ItemVariant>> views;

    public static ItemInventory create(Storage<ItemVariant> storage) {
        if (storage instanceof SlottedStorage<?> rawSlotted) {
            @SuppressWarnings("unchecked")
            SlottedStorage<ItemVariant> slotted = (SlottedStorage<ItemVariant>) rawSlotted;
            return new FabricStorageInventory(slotted, List.of());
        }
        ArrayList<StorageView<ItemVariant>> storageViews = new ArrayList<>();
        for (StorageView<ItemVariant> view : storage) {
            storageViews.add(view);
        }
        return new FabricStorageInventory(null, List.copyOf(storageViews));
    }

    private FabricStorageInventory(SlottedStorage<ItemVariant> slottedStorage, List<StorageView<ItemVariant>> views) {
        this.slottedStorage = slottedStorage;
        this.views = views;
    }

    @Override
    public int getSlots() {
        return slottedStorage == null ? views.size() : slottedStorage.getSlotCount();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        StorageView<ItemVariant> view = getView(slot);
        if (view.isResourceBlank() || view.getAmount() <= 0) {
            return ItemStack.EMPTY;
        }
        return view.getResource().toStack((int) Math.min(view.getAmount(), Integer.MAX_VALUE));
    }

    @Override
    public int getSlotLimit(int slot) {
        return (int) Math.min(getView(slot).getCapacity(), Integer.MAX_VALUE);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (slottedStorage == null || stack.isEmpty()) {
            return false;
        }
        try (Transaction transaction = Transaction.openOuter()) {
            return slottedStorage.getSlot(slot).insert(ItemVariant.of(stack), stack.getCount(), transaction) > 0;
        }
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (slottedStorage == null || stack.isEmpty()) {
            return stack;
        }
        try (Transaction transaction = Transaction.openOuter()) {
            long inserted = slottedStorage.getSlot(slot).insert(ItemVariant.of(stack), stack.getCount(), transaction);
            if (inserted > 0 && !simulate) {
                transaction.commit();
            }
            return stack.copyWithCount(stack.getCount() - (int) inserted);
        }
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slottedStorage == null || amount <= 0) {
            return ItemStack.EMPTY;
        }
        StorageView<ItemVariant> view = getView(slot);
        if (view.isResourceBlank()) {
            return ItemStack.EMPTY;
        }
        ItemVariant resource = view.getResource();
        try (Transaction transaction = Transaction.openOuter()) {
            long extracted = slottedStorage.getSlot(slot).extract(resource, amount, transaction);
            if (extracted > 0 && !simulate) {
                transaction.commit();
            }
            return resource.toStack((int) extracted);
        }
    }

    private StorageView<ItemVariant> getView(int slot) {
        if (slot < 0 || slot >= getSlots()) {
            throw new IndexOutOfBoundsException("Slot " + slot);
        }
        return slottedStorage == null ? views.get(slot) : slottedStorage.getSlot(slot);
    }
}
