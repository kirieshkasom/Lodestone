package team.lodestar.lodestone.modules.toolkit.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("NullableProblems")
public class LodestoneBlockEntityTicker<T extends LodestoneBlockEntity> implements BlockEntityTicker<T> {



    public enum Type {
        BOTH,
        CLIENT,
        SERVER,
        NONE;
    }
    protected final List<BlockEntityTickerAttachment> attachments;

    public LodestoneBlockEntityTicker() {
        this(Collections.emptyList());
    }

    public LodestoneBlockEntityTicker(List<BlockEntityTickerAttachment> attachments) {
        this.attachments = attachments;
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {
        if (level instanceof ServerLevel serverLevel) {
            blockEntity.serverTick(serverLevel);
        }
        else if (level.isClientSide) {
            blockEntity.clientTick(level);
        }
        blockEntity.commonTick(level);
        attachments.forEach(attachment -> attachment.tick(blockEntity, level, pos, state));
    }

    public interface BlockEntityTickerAttachment {
        void tick(LodestoneBlockEntity parent, Level level, BlockPos pos, BlockState state);
    }
}
