package team.lodestar.lodestone.modules.toolkit.inventory;

public class InventoryInteractionResultBuilder {

    private final InventoryInteractionResult.InteractionType interactionType;
    private InventoryItemStackTransaction internalChange;
    private InventoryItemStackTransaction externalChange;

    public InventoryInteractionResultBuilder(InventoryInteractionResult.InteractionType interactionType) {
        this.interactionType = interactionType;
    }

    public InventoryInteractionResultBuilder internalChange(InventoryItemStackTransaction internalChange) {
        this.internalChange = internalChange;
        return this;
    }

    public InventoryInteractionResultBuilder externalChange(InventoryItemStackTransaction externalChange) {
        this.externalChange = externalChange;
        return this;
    }

    public InventoryInteractionResult build() {
        return new InventoryInteractionResult(internalChange, externalChange, interactionType);
    }

}
