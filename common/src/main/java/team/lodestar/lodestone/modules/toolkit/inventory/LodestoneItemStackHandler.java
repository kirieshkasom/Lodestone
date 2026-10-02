package team.lodestar.lodestone.modules.toolkit.inventory;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.*;

/**
 * An extension of the ItemStackHandler class, designed to work well when several inventories are relevant in a singular context, such as a block that stores multiple types of items in different lists.
 */
public class LodestoneItemStackHandler extends ItemStackHandler {

    protected final int slotCount;
    protected final int allowedItemSize;
    protected final BiPredicate<LodestoneItemStackHandler, ItemStack> inputPredicate;
    protected final Runnable contentsChangeBehavior;

    protected ArrayList<ItemStack> nonEmptyItemStacks = new ArrayList<>();

    private int filledSlots;

    public static LodestoneItemStackHandlerBuilder create(int slotCount) {
        return new LodestoneItemStackHandlerBuilder(slotCount);
    }

    public LodestoneItemStackHandler(int slotCount, int allowedItemSize, BiPredicate<LodestoneItemStackHandler, ItemStack> inputPredicate, Runnable contentsChangeBehavior) {
        super(slotCount);
        this.slotCount = slotCount;
        this.allowedItemSize = allowedItemSize;
        this.inputPredicate = inputPredicate;
        this.contentsChangeBehavior = contentsChangeBehavior;
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

    @Override
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
    public int getSlotLimit(int slot) {
        return allowedItemSize;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        if (!getInputPredicate().test(this, stack)) {
            return false;
        }
        return super.isItemValid(slot, stack);
    }

    public void updateCaches() {
        nonEmptyItemStacks.clear();
        filledSlots = 0;
        for (ItemStack stack : getStacks()) {
            if (!stack.isEmpty()) {
                nonEmptyItemStacks.add(stack);
                filledSlots++;
            }
        }
    }

    public void ensureSize() {
        int slots = getSlotCount();
        if (stacks.size() != slots) {
            var updated = NonNullList.withSize(slots, ItemStack.EMPTY);
            for (int i = 0; i < stacks.size(); i++) {
                if (i >= slots) {
                    continue;
                }
                updated.set(i, stacks.get(i));
            }
            stacks = updated;
        }
    }

    public void load(HolderLookup.Provider provider, CompoundTag compound) {
        load(provider, compound, "inventory");
    }

    public void load(HolderLookup.Provider provider, CompoundTag compound, String name) {
        ensureSize();
        deserializeNBT(provider, compound.getCompound(name));
        updateCaches();
    }

    public void save(HolderLookup.Provider provider, CompoundTag compound) {
        save(provider, compound, "inventory");
    }

    public void save(HolderLookup.Provider provider, CompoundTag compound, String name) {
        compound.put(name, serializeNBT(provider));
    }

    public void clear() {
        ensureSize();
        for (int i = 0; i < getSlotCount(); i++) {
            setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    public void dumpItems(Level level, BlockPos pos) {
        dumpItems(level, pos.getCenter());
    }

    public void dumpItems(Level level, Vec3 pos) {
        ensureSize();
        for (int i = 0; i < getSlotCount(); i++) {
            if (!getStackInSlot(i).isEmpty()) {
                level.addFreshEntity(new ItemEntity(level, pos.x(), pos.y(), pos.z(), getStackInSlot(i)));
                setStackInSlot(i, ItemStack.EMPTY);
            }
        }
    }

    public final boolean interact(ServerLevel level, Player player, InteractionHand hand) {
        var result = performInteraction(level, player, hand);
        return result.map(InventoryInteractionResult::wasSuccessful).orElse(false);
    }

    public final Optional<InventoryInteractionResult> performInteraction(ServerLevel level, Player player, InteractionHand hand) {
        return performInteraction(level, player, player.getItemInHand(hand));
    }

    public Optional<InventoryInteractionResult> performInteraction(ServerLevel level, Player player, ItemStack heldStack) {
        updateCaches();
        if (heldStack.isEmpty()) {
            var extract = extractItem(level);
            ItemHandlerHelper.giveItemToPlayer(player, extract.externalChanges().getUpdated());
            if (extract.wasSuccessful()) {
                return Optional.of(extract);
            }
        } else {
            var insert = insertItem(level, heldStack);
            if (insert.wasSuccessful()) {
                return Optional.of(insert);
            }
        }
        return Optional.empty();
    }

    public InventoryInteractionResult extractItem(ServerLevel level) {
        return extractItem(level, ItemStack::getCount);
    }

    public InventoryInteractionResult extractItem(ServerLevel level, int amount) {
        return extractItem(level, s -> amount);
    }

    public InventoryInteractionResult extractItem(ServerLevel level, Function<ItemStack, Integer> amount) {
        if (isEmpty()) {
            return InventoryInteractionResult.EMPTY;
        }
        var toExtract = nonEmptyItemStacks.getLast();
        int slot = stacks.indexOf(toExtract);
        var extracted = amount.apply(toExtract);
        var simulated = extractItem(slot, extracted, true);
        if (simulated.equals(ItemStack.EMPTY)) {
            return InventoryInteractionResult.EMPTY;
        }
        var real = extractItem(slot, extracted, false);
        var leftover = real.copyWithCount(real.getCount() - extracted);

        var builder = InventoryInteractionResult.extract()
                .internalChange(InventoryItemStackTransaction.updated(toExtract, leftover, slot))
                .externalChange(InventoryItemStackTransaction.updated(ItemStack.EMPTY, real, slot));
        var result = builder.build();
        processResult(level, result);
        return result;
    }

    public InventoryInteractionResult insertItem(ServerLevel level, ItemStack stack) {
        var simulated = insertItem(level, stack, true);
        if (!simulated.wasSuccessful()) {
            return simulated;
        }
        var internalChanges = simulated.internalChanges();
        int count = internalChanges.getExchangedCount(getAllowedItemSize());
        var input = stack.split(count);
        return insertItem(level, input, false);
    }

    protected InventoryInteractionResult insertItem(ServerLevel level, ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) {
            return InventoryInteractionResult.EMPTY;
        }

        var untouched = stack.copy();
        var builder = InventoryInteractionResult.insert();
        for (int i = 0; i < getSlots(); i++) {
            var current = getStackInSlot(0);
            stack = insertItem(i, stack, simulate);
            var inserted = untouched.copyWithCount(untouched.getCount()-stack.getCount());
            builder.internalChange(InventoryItemStackTransaction.updated(current, inserted, i));
            if (stack.isEmpty()) {
                break;
            }
        }
        builder.externalChange(InventoryItemStackTransaction.updated(untouched, stack, -1));
        var result = builder.build();
        processResult(level, result);
        return result;
    }

    protected void processResult(ServerLevel level, InventoryInteractionResult result) {

    }
}