package team.lodestar.lodestone.modules.toolkit.inventory;

import net.minecraft.network.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import team.lodestar.lodestone.modules.toolkit.blockentity.*;

import javax.annotation.*;

public abstract class LodestoneBlockEntityContainer<T extends LodestoneBlockEntity> extends AbstractContainerMenu {

    public final T blockEntity;

    public LodestoneBlockEntityContainer(MenuType<?> menuType, int containerId, Inventory playerInventory, RegistryFriendlyByteBuf data) {
        this(menuType, containerId, playerInventory, ContainerLevelAccess.create(playerInventory.player.level(), data.readBlockPos()));
    }

    public LodestoneBlockEntityContainer(MenuType<?> menuType, int containerId, Inventory playerInventory) {
        this(menuType, containerId, playerInventory, ContainerLevelAccess.NULL);
    }

    public LodestoneBlockEntityContainer(MenuType<?> menuType, int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(menuType, containerId);

        var clazz = getBlockEntityClass();
        this.blockEntity = access
                .evaluate((Level::getBlockEntity))
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .orElse(null);

        int[] topLeft = getPlayerInventoryTopLeft();
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                int index = x + (y + 1) * 9;
                int xPos = topLeft[0] + x * 18;
                int yPos = topLeft[1] + y * 18;
                this.addSlot(new Slot(playerInventory, index, xPos, yPos));
            }
        }
        for (int x = 0; x < 9; ++x) {
            this.addSlot(new Slot(playerInventory, x, topLeft[0] + x * 18, topLeft[1] + 58));
        }
    }

    public abstract Class<T> getBlockEntityClass();

    public abstract LodestoneItemStackBlockHandler getItemStackHandler();

    public int[] getPlayerInventoryTopLeft() {
        return new int[]{8, 84};
    }

    @Nonnull
    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        var result = ItemStack.EMPTY;
        var slot = slots.get(index);
        var itemHandler = getItemStackHandler();
        if (slot.hasItem()) {
            ItemStack clickedItem = slot.getItem();
            result = clickedItem.copy();
            int handlerSlots = itemHandler.getSlots();
            int playerSlots = 36;
            if (index < playerSlots) {
                if (!moveItemStackTo(clickedItem, playerSlots, playerSlots + handlerSlots, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else {
                if (!moveItemStackTo(clickedItem, 0, playerSlots, true)) {
                    return ItemStack.EMPTY;
                }
            }

            if (clickedItem.isEmpty()) {
                slot.set(ItemStack.EMPTY);
                slot.onTake(playerIn, clickedItem);
            } else {
                slot.setChanged();
            }
        }
        itemHandler.onContentsChanged(index);
        return result;
    }
}