package team.lodestar.lodestone.modules.toolkit.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.lodestar.lodestone.modules.toolkit.inventory.ItemInventory;
import team.lodestar.lodestone.modules.toolkit.inventory.InventoryAccess;
import team.lodestar.lodestone.modules.toolkit.block.LodestoneEntityBlock;

import java.util.Optional;

/**
 * A basic Multiblock component block.
 */
@SuppressWarnings("NullableProblems")
public class MultiblockComponentBlock extends LodestoneEntityBlock<MultiBlockComponentEntity> implements ILodestoneMultiblockComponent {

    public MultiblockComponentBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof MultiBlockComponentEntity provider) {
            Optional<MultiBlockCoreEntity> optional = provider.getCore();
            if (optional.isEmpty()) {
                return 0;
            }
            MultiBlockCoreEntity core = optional.get();
            ItemInventory inventory = InventoryAccess.get(level, core.getBlockPos(), null);
            if (inventory != null) {
                return calculateRedstoneSignal(inventory);
            }
        }
        return 0;
    }

    private int calculateRedstoneSignal(ItemInventory inventory) {
        if (inventory.getSlots() == 0) {
            return 0;
        }
        float fullness = 0.0F;
        int nonEmptySlots = 0;
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                fullness += (float) stack.getCount() / (float) Math.min(inventory.getSlotLimit(slot), stack.getMaxStackSize());
                nonEmptySlots++;
            }
        }
        return Math.min(15, (int) (fullness / inventory.getSlots() * 14.0F) + (nonEmptySlots > 0 ? 1 : 0));
    }
}
