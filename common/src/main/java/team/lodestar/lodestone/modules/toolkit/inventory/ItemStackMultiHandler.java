package team.lodestar.lodestone.modules.toolkit.inventory;

import com.google.common.collect.ImmutableList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Optional;

public class ItemStackMultiHandler implements ItemInventory {

    protected final ImmutableList<LodestoneItemStackHandler> inventories;

    protected int recentInteractionIndex;

    public ItemStackMultiHandler(LodestoneItemStackHandler... inventories) {
        this.inventories = ImmutableList.<LodestoneItemStackHandler>builder().add(inventories).build();
    }

    public ImmutableList<LodestoneItemStackHandler> getInventories() {
        return inventories;
    }

    public boolean interact(ServerLevel level, Player player, InteractionHand hand) {
        ArrayList<LodestoneItemStackHandler> interactionQueue = new ArrayList<>(inventories);
        if (recentInteractionIndex != -1) {
            LodestoneItemStackHandler recentHandler = inventories.get(recentInteractionIndex);
            interactionQueue.remove(recentHandler);
            Optional<InventoryInteractionResult> result = interact(level, recentHandler, player, hand);
            if (result.map(InventoryInteractionResult::wasSuccessful).orElse(false)) {
                return true;
            } else {
                recentInteractionIndex = -1;
            }
        }
        for (LodestoneItemStackHandler handler : interactionQueue) {
            Optional<InventoryInteractionResult> result = interact(level, handler, player, hand);
            if (result.map(InventoryInteractionResult::wasSuccessful).orElse(false)) {
                recentInteractionIndex = inventories.indexOf(handler);
                return true;
            }
        }
        return false;
    }

    public Optional<InventoryInteractionResult> interact(ServerLevel level, LodestoneItemStackHandler handler, Player player, InteractionHand hand) {
        return handler.performInteraction(level, player, hand);
    }

    @Override
    public int getSlots() {
        int slots = 0;
        for (LodestoneItemStackHandler inventory : inventories) {
            slots += inventory.getSlots();
        }
        return slots;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        SlotRef ref = findSlot(slot);
        return ref.inventory().getStackInSlot(ref.slot());
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        SlotRef ref = findSlot(slot);
        ref.inventory().setStackInSlot(ref.slot(), stack);
    }

    @Override
    public int getSlotLimit(int slot) {
        SlotRef ref = findSlot(slot);
        return ref.inventory().getSlotLimit(ref.slot());
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        SlotRef ref = findSlot(slot);
        return ref.inventory().isItemValid(ref.slot(), stack);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        SlotRef ref = findSlot(slot);
        return ref.inventory().insertItem(ref.slot(), stack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        SlotRef ref = findSlot(slot);
        return ref.inventory().extractItem(ref.slot(), amount, simulate);
    }

    private SlotRef findSlot(int slot) {
        if (slot < 0) {
            throw new IndexOutOfBoundsException("Slot " + slot);
        }
        int offset = slot;
        for (LodestoneItemStackHandler inventory : inventories) {
            if (offset < inventory.getSlots()) {
                return new SlotRef(inventory, offset);
            }
            offset -= inventory.getSlots();
        }
        throw new IndexOutOfBoundsException("Slot " + slot + " outside combined inventory size " + getSlots());
    }

    private record SlotRef(LodestoneItemStackHandler inventory, int slot) {
    }
}
