package team.lodestar.lodestone.modules.toolkit.creative_tab.slot;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SlotStorage {

    protected final int row;
    protected final int column;
    protected final int itemIndex;

    protected final ItemStack selectedItem;
    protected final List<ItemStack> storedItems;

    public SlotStorage(SlotLocation location, ItemStack... stacks) {
        this(location.row, location.column, location.index, stacks);
    }
    public SlotStorage(int row, int column, int itemIndex, ItemStack... stacks) {
        this.row = row;
        this.column = column;
        this.itemIndex = itemIndex;
        this.storedItems = List.of(stacks);
        this.selectedItem = storedItems.isEmpty() ? ItemStack.EMPTY : storedItems.getFirst();
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public int getItemIndex() {
        return itemIndex;
    }

    public ItemStack getSelectedItem() {
        return selectedItem;
    }

    public List<ItemStack> getStoredItems() {
        return storedItems;
    }
}
