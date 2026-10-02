package team.lodestar.lodestone.modules.toolkit.inventory;

import net.minecraft.world.item.ItemStack;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class LodestoneItemStackHandlerBuilder {

    public final int slotCount;
    public int allowedItemSize = 64;
    public BiPredicate<LodestoneItemStackHandler, ItemStack> inputPredicate = (h, s) -> true;
    public Runnable onContentsChanged;

    protected LodestoneItemStackHandlerBuilder(int slotCount) {
        this.slotCount = slotCount;
    }

    public LodestoneItemStackHandlerBuilder limitItemSize(int allowedItemSize) {
        this.allowedItemSize = allowedItemSize;
        return this;
    }

    public LodestoneItemStackHandlerBuilder setInputPredicate(Predicate<ItemStack> inputPredicate) {
        return setInputPredicate((h, s) -> inputPredicate.test(s));
    }

    public LodestoneItemStackHandlerBuilder setInputPredicate(BiPredicate<LodestoneItemStackHandler, ItemStack> inputPredicate) {
        this.inputPredicate = inputPredicate;
        return this;
    }

    public LodestoneItemStackHandlerBuilder onContentsChanged(Runnable contentsChangeBehavior) {
        this.onContentsChanged = contentsChangeBehavior;
        return this;
    }

    public LodestoneItemStackHandler build() {
        return new LodestoneItemStackHandler(slotCount, allowedItemSize, inputPredicate, onContentsChanged);
    }
}