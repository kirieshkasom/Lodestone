package team.lodestar.lodestone.modules.toolkit.inventory;

import net.minecraft.world.item.ItemStack;
import team.lodestar.lodestone.modules.toolkit.blockentity.LodestoneBlockEntity;

import java.util.function.BiPredicate;
import java.util.function.Function;

public class LodestoneItemStackBlockHandler extends LodestoneItemStackHandler {

    protected final LodestoneBlockEntity parent;
    protected ItemStackHandlerItemDisplayData displayData;

    public LodestoneItemStackBlockHandler(LodestoneBlockEntity parent, int slotCount, int allowedItemSize, BiPredicate<LodestoneItemStackHandler, ItemStack> inputPredicate, Runnable contentsChangeBehavior) {
        super(slotCount, allowedItemSize, inputPredicate, contentsChangeBehavior);
        this.parent = parent;
    }

    public void attachDisplayData(Function<LodestoneItemStackBlockHandler, ItemStackHandlerItemDisplayData> displayData) {
        attachDisplayData(displayData.apply(this));
    }

    public void attachDisplayData(ItemStackHandlerItemDisplayData displayData) {
        assert this.displayData == null;
        this.displayData = displayData;
        parent.attachTicker(displayData);
    }

    @Override
    public void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        parent.setDirty();
        if (displayData != null) {
            displayData.onContentsChanged(slot);
        }
    }

    public LodestoneBlockEntity getParent() {
        return parent;
    }

    public ItemStackHandlerItemDisplayData getDisplayData() {
        return displayData;
    }
}
