package team.lodestar.lodestone.modules.toolkit.blockentity;

import com.google.common.collect.ImmutableList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import team.lodestar.lodestone.modules.toolkit.block.LodestoneEntityBlock;

import java.util.Collections;
import java.util.Set;

public class LodestoneBlockEntityType<T extends LodestoneBlockEntity> extends BlockEntityType<T> {

    protected final LodestoneBlockEntityTicker.Type type;

    @SuppressWarnings("DataFlowIssue")
    public LodestoneBlockEntityType(LodestoneBlockEntityTypeBuilder.LodestoneBlockEntitySupplier<? extends T> factory, Set<Block> validBlocks, LodestoneBlockEntityTicker.Type type) {
        super(factory, validBlocks, null);
        this.type = type;
        for (Block validBlock : validBlocks) {
            if (validBlock instanceof LodestoneEntityBlock<?> entityBlock) {
                entityBlock.setBlockEntity(this);
            }
        }
    }

    public final LodestoneBlockEntityTicker<T> getTickerUnsafe(Level level, BlockState state) {
        if (hasTicker(level)) {
            return new LodestoneBlockEntityTicker<>();
        }
        return null;
    }

    public final LodestoneBlockEntityTicker<T> getBlockEntityAwareTicker(Level level, BlockState state, LodestoneBlockEntity blockEntity) {
        if (hasTicker(level)) {
            if (blockEntity.tickers == null) {
                return new LodestoneBlockEntityTicker<>();
            }
            return new LodestoneBlockEntityTicker<>(ImmutableList.copyOf(blockEntity.tickers));
        }
        return null;
    }

    public boolean hasTicker(Level level) {
        return switch (type) {
            case BOTH -> true;
            case NONE -> false;
            case SERVER -> level instanceof ServerLevel;
            case CLIENT -> level.isClientSide;
        };
    }
}
