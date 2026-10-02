package team.lodestar.lodestone.modules.toolkit.inventory;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;

/** A portable item inventory with insertion filters, cached contents, and interaction helpers. */
public class LodestoneItemStackHandler implements ItemInventory {
    protected final int slotCount;
    protected final int allowedItemSize;
    protected final BiPredicate<LodestoneItemStackHandler, ItemStack> inputPredicate;
    protected final Runnable contentsChangeBehavior;
    protected NonNullList<ItemStack> stacks;
    protected ArrayList<ItemStack> nonEmptyItemStacks = new ArrayList<>();
    private int filledSlots;

    public static LodestoneItemStackHandlerBuilder create(int slotCount) {
        return new LodestoneItemStackHandlerBuilder(slotCount);
    }

    public LodestoneItemStackHandler(int slotCount, int allowedItemSize, BiPredicate<LodestoneItemStackHandler, ItemStack> inputPredicate, Runnable contentsChangeBehavior) {
        if (slotCount < 0 || allowedItemSize < 0) {
            throw new IllegalArgumentException("Inventory dimensions must be non-negative");
        }
        this.slotCount = slotCount;
        this.allowedItemSize = allowedItemSize;
        this.inputPredicate = inputPredicate;
        this.contentsChangeBehavior = contentsChangeBehavior;
        this.stacks = NonNullList.withSize(slotCount, ItemStack.EMPTY);
    }

    public int getSlotCount() {
        return slotCount;
    }

    public int getAllowedItemSize() {
        return allowedItemSize;
    }

    public BiPredicate<LodestoneItemStackHandler, ItemStack> getInputPredicate() {
        return inputPredicate;
    }

    public NonNullList<ItemStack> getStacks() {
        return stacks;
    }

    public ArrayList<ItemStack> getNonEmptyStacks() {
        return nonEmptyItemStacks;
    }

    public int getFilledSlotCount() {
        return filledSlots;
    }

    public boolean isEmpty() {
        return nonEmptyItemStacks.isEmpty();
    }

    public void onContentsChanged(int slot) {
        updateCaches();
        if (contentsChangeBehavior != null) {
            contentsChangeBehavior.run();
        }
    }

    @Override
    public int getSlots() {
        return slotCount;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        checkSlot(slot);
        return stacks.get(slot);
    }

    @Override
    public int getSlotLimit(int slot) {
        checkSlot(slot);
        return allowedItemSize;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        checkSlot(slot);
        return stack.isEmpty() || inputPredicate.test(this, stack);
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        checkSlot(slot);
        if (stack.getCount() > Math.min(getSlotLimit(slot), stack.getMaxStackSize())) {
            throw new IllegalArgumentException("Stack exceeds slot limit");
        }
        stacks.set(slot, stack.isEmpty() ? ItemStack.EMPTY : stack);
        onContentsChanged(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        checkSlot(slot);
        if (stack.isEmpty() || !isItemValid(slot, stack)) {
            return stack;
        }
        ItemStack existing = stacks.get(slot);
        if (!existing.isEmpty() && !ItemStack.isSameItemSameComponents(existing, stack)) {
            return stack;
        }
        int limit = Math.min(getSlotLimit(slot), stack.getMaxStackSize());
        int space = limit - (existing.isEmpty() ? 0 : existing.getCount());
        if (space <= 0) {
            return stack;
        }
        int inserted = Math.min(space, stack.getCount());
        ItemStack remainder = stack.copyWithCount(stack.getCount() - inserted);
        if (!simulate) {
            if (existing.isEmpty()) {
                stacks.set(slot, stack.copyWithCount(inserted));
            } else {
                existing.grow(inserted);
            }
            onContentsChanged(slot);
        }
        return remainder;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        checkSlot(slot);
        if (amount <= 0) {
            return ItemStack.EMPTY;
        }
        ItemStack existing = stacks.get(slot);
        if (existing.isEmpty()) {
            return ItemStack.EMPTY;
        }
        int extracted = Math.min(amount, existing.getCount());
        ItemStack result = existing.copyWithCount(extracted);
        if (!simulate) {
            if (extracted == existing.getCount()) {
                stacks.set(slot, ItemStack.EMPTY);
            } else {
                existing.shrink(extracted);
            }
            onContentsChanged(slot);
        }
        return result;
    }

    public void updateCaches() {
        nonEmptyItemStacks.clear();
        filledSlots = 0;
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty()) {
                nonEmptyItemStacks.add(stack);
                filledSlots++;
            }
        }
    }

    public void ensureSize() {
        if (stacks.size() != slotCount) {
            NonNullList<ItemStack> updated = NonNullList.withSize(slotCount, ItemStack.EMPTY);
            for (int i = 0; i < Math.min(stacks.size(), slotCount); i++) {
                updated.set(i, stacks.get(i));
            }
            stacks = updated;
            updateCaches();
        }
    }

    public void load(HolderLookup.Provider provider, CompoundTag compound) {
        load(provider, compound, "inventory");
    }

    public void load(HolderLookup.Provider provider, CompoundTag compound, String name) {
        ensureSize();
        CompoundTag inventory = compound.getCompound(name);
        ListTag items = inventory.getList("Items", Tag.TAG_COMPOUND);
        for (int slot = 0; slot < slotCount; slot++) {
            stacks.set(slot, ItemStack.EMPTY);
        }
        for (int i = 0; i < items.size(); i++) {
            CompoundTag itemTag = items.getCompound(i);
            int slot = itemTag.getInt("Slot");
            if (slot >= 0 && slot < slotCount) {
                ItemStack stack = ItemStack.parseOptional(provider, itemTag);
                if (!stack.isEmpty()) {
                    stacks.set(slot, stack);
                }
            }
        }
        updateCaches();
    }

    public void save(HolderLookup.Provider provider, CompoundTag compound) {
        save(provider, compound, "inventory");
    }

    public void save(HolderLookup.Provider provider, CompoundTag compound, String name) {
        CompoundTag inventory = new CompoundTag();
        inventory.putInt("Size", slotCount);
        ListTag items = new ListTag();
        for (int slot = 0; slot < slotCount; slot++) {
            ItemStack stack = stacks.get(slot);
            if (!stack.isEmpty()) {
                CompoundTag itemTag = (CompoundTag) stack.save(provider);
                itemTag.putInt("Slot", slot);
                items.add(itemTag);
            }
        }
        inventory.put("Items", items);
        compound.put(name, inventory);
    }

    public void clear() {
        for (int i = 0; i < slotCount; i++) {
            if (!stacks.get(i).isEmpty()) {
                stacks.set(i, ItemStack.EMPTY);
                onContentsChanged(i);
            }
        }
    }

    public void dumpItems(Level level, BlockPos pos) {
        dumpItems(level, pos.getCenter());
    }

    public void dumpItems(Level level, Vec3 pos) {
        for (int i = 0; i < slotCount; i++) {
            ItemStack stack = extractItem(i, Integer.MAX_VALUE, false);
            if (!stack.isEmpty()) {
                level.addFreshEntity(new ItemEntity(level, pos.x(), pos.y(), pos.z(), stack));
            }
        }
    }

    public final boolean interact(ServerLevel level, Player player, InteractionHand hand) {
        Optional<InventoryInteractionResult> result = performInteraction(level, player, hand);
        return result.map(InventoryInteractionResult::wasSuccessful).orElse(false);
    }

    public final Optional<InventoryInteractionResult> performInteraction(ServerLevel level, Player player, InteractionHand hand) {
        return performInteraction(level, player, player.getItemInHand(hand));
    }

    public Optional<InventoryInteractionResult> performInteraction(ServerLevel level, Player player, ItemStack heldStack) {
        updateCaches();
        if (heldStack.isEmpty()) {
            InventoryInteractionResult result = extractItem(level);
            if (result.wasSuccessful()) {
                ItemStack extracted = result.externalChanges().getUpdated();
                player.getInventory().placeItemBackInInventory(extracted);
                return Optional.of(result);
            }
        } else {
            InventoryInteractionResult result = insertItem(level, heldStack);
            if (result.wasSuccessful()) {
                return Optional.of(result);
            }
        }
        return Optional.empty();
    }

    public InventoryInteractionResult extractItem(ServerLevel level) {
        return extractItem(level, ItemStack::getCount);
    }

    public InventoryInteractionResult extractItem(ServerLevel level, int amount) {
        return extractItem(level, stack -> amount);
    }

    public InventoryInteractionResult extractItem(ServerLevel level, Function<ItemStack, Integer> amount) {
        if (isEmpty()) {
            return InventoryInteractionResult.EMPTY;
        }
        ItemStack toExtract = nonEmptyItemStacks.get(nonEmptyItemStacks.size() - 1);
        int slot = stacks.indexOf(toExtract);
        ItemStack original = toExtract.copy();
        int requested = Math.max(0, amount.apply(toExtract));
        ItemStack simulated = extractItem(slot, requested, true);
        if (simulated.isEmpty()) {
            return InventoryInteractionResult.EMPTY;
        }
        ItemStack real = extractItem(slot, requested, false);
        ItemStack leftover = stacks.get(slot).copy();
        InventoryInteractionResult result = InventoryInteractionResult.extract()
                .internalChange(InventoryItemStackTransaction.updated(original, leftover, slot))
                .externalChange(InventoryItemStackTransaction.updated(ItemStack.EMPTY, real, slot))
                .build();
        processResult(level, result);
        return result;
    }

    public InventoryInteractionResult insertItem(ServerLevel level, ItemStack stack) {
        ItemStack original = stack.copy();
        ItemStack remainder = stack;
        for (int i = 0; i < slotCount && !remainder.isEmpty(); i++) {
            remainder = insertItem(i, remainder, false);
        }
        ItemStack inserted = original.copyWithCount(original.getCount() - remainder.getCount());
        if (inserted.isEmpty()) {
            return InventoryInteractionResult.EMPTY;
        }
        stack.shrink(inserted.getCount());
        InventoryInteractionResult result = InventoryInteractionResult.insert()
                .internalChange(InventoryItemStackTransaction.updated(ItemStack.EMPTY, inserted, -1))
                .externalChange(InventoryItemStackTransaction.updated(original, stack, -1))
                .build();
        processResult(level, result);
        return result;
    }

    protected void processResult(ServerLevel level, InventoryInteractionResult result) {
    }

    private void checkSlot(int slot) {
        if (slot < 0 || slot >= slotCount) {
            throw new IndexOutOfBoundsException("Slot " + slot + " outside inventory size " + slotCount);
        }
    }
}
