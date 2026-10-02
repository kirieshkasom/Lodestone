package team.lodestar.lodestone.modules.toolkit.inventory;

import com.google.common.collect.ImmutableList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;

import java.util.ArrayList;
import java.util.Optional;

public class ItemStackMultiHandler extends CombinedInvWrapper {

    protected final ImmutableList<LodestoneItemStackHandler> inventories;

    protected int recentInteractionIndex;

    public ItemStackMultiHandler(LodestoneItemStackHandler... inventories) {
        super(inventories);
        this.inventories = ImmutableList.<LodestoneItemStackHandler>builder().add(inventories).build();
    }

    public ImmutableList<LodestoneItemStackHandler> getInventories() {
        return inventories;
    }

    public boolean interact(ServerLevel level, Player player, InteractionHand hand) {
        var interactionQueue = new ArrayList<>(inventories);
        if (recentInteractionIndex != -1) {
            var recentHandler = inventories.get(recentInteractionIndex);
            interactionQueue.remove(recentHandler);
            var result = interact(level, recentHandler, player, hand);
            if (result.map(InventoryInteractionResult::wasSuccessful).orElse(false)) {
                return true;
            } else {
                recentInteractionIndex = -1;
            }
        }
        for (LodestoneItemStackHandler handler : interactionQueue) {
            var result = interact(level, handler, player, hand);
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
}