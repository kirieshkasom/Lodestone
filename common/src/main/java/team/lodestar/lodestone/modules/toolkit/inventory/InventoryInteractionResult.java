package team.lodestar.lodestone.modules.toolkit.inventory;

import net.minecraft.world.item.*;

public record InventoryInteractionResult(
        InventoryItemStackTransaction internalChanges,
        InventoryItemStackTransaction externalChanges,
        InventoryInteractionResult.InteractionType interactionType) {

    public enum InteractionType {
        INSERT,
        EXTRACT,
        OTHER
    }

    public static InventoryInteractionResultBuilder extract() {
        return create(InteractionType.EXTRACT);
    }

    public static InventoryInteractionResultBuilder insert() {
        return create(InteractionType.INSERT);
    }

    public static InventoryInteractionResultBuilder create(InteractionType interactionType) {
        return new InventoryInteractionResultBuilder(interactionType);
    }

    public static final InventoryInteractionResult EMPTY = InventoryInteractionResult.create(InteractionType.OTHER).build();

    public boolean wasSuccessful() {
        return internalChanges.wasSuccessful() || externalChanges.wasSuccessful();
    }

    public ItemStack getTransferredItem() {
        return switch (interactionType()) {
            case INSERT -> internalChanges.getNonEmpty();
            case EXTRACT -> externalChanges.getNonEmpty();
            case OTHER -> ItemStack.EMPTY;
        };
    }

}