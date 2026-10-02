package team.lodestar.lodestone.modules.toolkit.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.lodestar.lodestone.modules.toolkit.blockentity.IInventoryCapabilityProvider;
import team.lodestar.lodestone.registry.common.*;
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
            var optional = provider.getCore();
            if (optional.isEmpty()) {
                return 0;
            }
            var core = optional.get();
            var capability = level.getCapability(Capabilities.ItemHandler.BLOCK, core.getBlockPos(), core.getBlockState(), core, null);
            if (capability != null) {
                return ItemHandlerHelper.calcRedstoneFromInventory(capability);
            }
        }
        return 0;
    }
}
