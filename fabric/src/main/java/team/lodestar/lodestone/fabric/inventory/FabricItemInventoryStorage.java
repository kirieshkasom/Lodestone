package team.lodestar.lodestone.fabric.inventory;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.world.item.ItemStack;
import team.lodestar.lodestone.modules.toolkit.inventory.ItemInventory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class FabricItemInventoryStorage implements SlottedStorage<ItemVariant> {
    private final ItemInventory inventory;
    private final List<SingleSlotStorage<ItemVariant>> slots;

    public FabricItemInventoryStorage(ItemInventory inventory) {
        this.inventory = inventory;
        ArrayList<SingleSlotStorage<ItemVariant>> storageSlots = new ArrayList<>();
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            storageSlots.add(new SlotStorage(slot));
        }
        this.slots = List.copyOf(storageSlots);
    }

    @Override
    public int getSlotCount() {
        return slots.size();
    }

    @Override
    public SingleSlotStorage<ItemVariant> getSlot(int slot) {
        return slots.get(slot);
    }

    @Override
    public Iterator<StorageView<ItemVariant>> iterator() {
        ArrayList<StorageView<ItemVariant>> storageViews = new ArrayList<>(slots);
        return storageViews.iterator();
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        if (resource.isBlank() || maxAmount <= 0) {
            return 0;
        }
        long inserted = 0;
        for (SingleSlotStorage<ItemVariant> slot : slots) {
            inserted += slot.insert(resource, maxAmount - inserted, transaction);
            if (inserted >= maxAmount) {
                break;
            }
        }
        return inserted;
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        if (resource.isBlank() || maxAmount <= 0) {
            return 0;
        }
        long extracted = 0;
        for (SingleSlotStorage<ItemVariant> slot : slots) {
            extracted += slot.extract(resource, maxAmount - extracted, transaction);
            if (extracted >= maxAmount) {
                break;
            }
        }
        return extracted;
    }

    private final class SlotStorage extends SnapshotParticipant<ItemStack> implements SingleSlotStorage<ItemVariant> {
        private final int slot;

        private SlotStorage(int slot) {
            this.slot = slot;
        }

        @Override
        public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            if (resource.isBlank() || maxAmount <= 0) {
                return 0;
            }
            ItemStack offered = resource.toStack((int) Math.min(maxAmount, Integer.MAX_VALUE));
            ItemStack remainder = inventory.insertItem(slot, offered, true);
            int inserted = offered.getCount() - remainder.getCount();
            if (inserted > 0) {
                updateSnapshots(transaction);
                ItemStack actualRemainder = inventory.insertItem(slot, resource.toStack(inserted), false);
                inserted -= actualRemainder.getCount();
            }
            return inserted;
        }

        @Override
        public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            if (resource.isBlank() || maxAmount <= 0) {
                return 0;
            }
            ItemStack current = inventory.getStackInSlot(slot);
            if (current.isEmpty() || !resource.matches(current)) {
                return 0;
            }
            int requested = (int) Math.min(maxAmount, current.getCount());
            ItemStack simulated = inventory.extractItem(slot, requested, true);
            if (simulated.isEmpty()) {
                return 0;
            }
            updateSnapshots(transaction);
            ItemStack extracted = inventory.extractItem(slot, simulated.getCount(), false);
            return extracted.getCount();
        }

        @Override
        public boolean isResourceBlank() {
            return inventory.getStackInSlot(slot).isEmpty();
        }

        @Override
        public ItemVariant getResource() {
            ItemStack stack = inventory.getStackInSlot(slot);
            return stack.isEmpty() ? ItemVariant.blank() : ItemVariant.of(stack);
        }

        @Override
        public long getAmount() {
            return inventory.getStackInSlot(slot).getCount();
        }

        @Override
        public long getCapacity() {
            ItemStack stack = inventory.getStackInSlot(slot);
            return Math.min(inventory.getSlotLimit(slot), stack.isEmpty() ? 64 : stack.getMaxStackSize());
        }

        @Override
        protected ItemStack createSnapshot() {
            return inventory.getStackInSlot(slot).copy();
        }

        @Override
        protected void readSnapshot(ItemStack snapshot) {
            inventory.setStackInSlot(slot, snapshot);
        }
    }
}
