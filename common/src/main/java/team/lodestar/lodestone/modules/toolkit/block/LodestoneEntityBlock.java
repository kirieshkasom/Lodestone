package team.lodestar.lodestone.modules.toolkit.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.lodestar.lodestone.modules.toolkit.blockentity.LodestoneBlockEntity;
import team.lodestar.lodestone.modules.toolkit.blockentity.LodestoneBlockEntityTicker;
import team.lodestar.lodestone.modules.toolkit.blockentity.LodestoneBlockEntityType;

import java.util.function.Supplier;

/**
 * A SimpleBlock is an implementation of EntityBlock that allows most frequently used logic to be handled in a SimpleBlockEntity
 * It's important to still utilize a generic, T extends YourBlockEntity, in order to allow for other mods to extend your block and use a different block entity
 */
@SuppressWarnings({"unchecked", "NullableProblems"})
public class LodestoneEntityBlock<T extends LodestoneBlockEntity> extends Block implements EntityBlock {

    protected Supplier<LodestoneBlockEntityType<T>> blockEntityType = null;

    public LodestoneEntityBlock(Properties properties) {
        super(properties);
    }

    public void setBlockEntity(LodestoneBlockEntityType<?> type) {
        this.blockEntityType = () -> (LodestoneBlockEntityType<T>) type;
    }

    public boolean hasBlockEntity() {
        return blockEntityType != null;
    }

    public T getBlockEntity(LevelReader level, BlockPos pos) {
        if (hasBlockEntity()) {
            return blockEntityType.get().getBlockEntity(level, pos);
        }
        return null;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return hasBlockEntity() ? blockEntityType.get().create(pos, state) : null;
    }

    @Nullable
    @Override
    public <Y extends BlockEntity> BlockEntityTicker<Y> getTicker(Level level, BlockState state, BlockEntityType<Y> blockEntityType) {
        return (BlockEntityTicker<Y>) ((LodestoneBlockEntityType<T>)blockEntityType).getTickerUnsafe(level, state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        var blockEntity = getBlockEntity(level, pos);
        if (blockEntity != null) {
            blockEntity.onPlace(placer, stack);
        }
        super.setPlacedBy(level, pos, state, placer, stack);
    }

    @Override
    @NotNull
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        var blockEntity = getBlockEntity(level, pos);
        if (blockEntity != null) {
            ItemStack stack = blockEntity.onClone(state, target, level, pos, player);
            if (!stack.isEmpty()) {
                return stack;
            }
        }
        return super.getCloneItemStack(state, target, level, pos, player);
    }

    @Override
    @NotNull
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        onBlockBroken(level, pos, player);
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
        onBlockBroken(level, pos, null);
        super.onBlockExploded(state, level, pos, explosion);
    }

    public void onBlockBroken(Level level, BlockPos pos, @Nullable Player player) {
        var blockEntity = getBlockEntity(level, pos);
        if (blockEntity != null) {
            blockEntity.onBreak(player);
        }
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        var blockEntity = getBlockEntity(level, pos);
        if (blockEntity != null) {
            blockEntity.onEntityInside(state, level, pos, entity);
        }
        super.entityInside(state, level, pos, entity);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
        var blockEntity = getBlockEntity(level, pos);
        if (blockEntity != null) {
            blockEntity.onNeighborUpdate(state, pos, neighborPos);
        }
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, isMoving);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
            var blockEntity = getBlockEntity(level, pos);
        if (blockEntity != null) {
            var earlyResult = blockEntity.onUseWithoutItem(player);
            return earlyResult.consumesAction() ? earlyResult : blockEntity.onUse(player, InteractionHand.MAIN_HAND).result();
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        var blockEntity = getBlockEntity(level, pos);
        if (blockEntity != null) {
            var earlyResult = blockEntity.onUseWithItem(player, stack, hand);
            return earlyResult.consumesAction() ? earlyResult : blockEntity.onUse(player, hand);
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
}
