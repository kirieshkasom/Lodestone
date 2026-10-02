package team.lodestar.lodestone.modules.toolkit.multiblock;

import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.items.*;
import org.jetbrains.annotations.Nullable;
import team.lodestar.lodestone.helpers.*;
import team.lodestar.lodestone.registry.common.*;
import team.lodestar.lodestone.modules.toolkit.blockentity.*;

import java.util.Optional;

/**
 * A basic Multiblock component block entity. Defers some important actions to the core of the multiblock.
 */
public class MultiBlockComponentEntity extends LodestoneBlockEntity implements IInventoryCapabilityProvider {

    public BlockPos corePos;

    public MultiBlockComponentEntity(LodestoneBlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public MultiBlockComponentEntity(BlockPos pos, BlockState state) {
        super(LodestoneBlockEntities.MULTIBLOCK_COMPONENT.get(), pos, state);
    }

    @Override
    public IItemHandler getInventory(Direction direction) {
        Optional<MultiBlockCoreEntity> optional = getCore();
        if (optional.isEmpty()) {
            return null;
        }
        if (optional.get() instanceof IInventoryCapabilityProvider provider) {
            return provider.getInventory(direction);
        }
        return null;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        if (corePos != null) {
            NBTHelper.saveBlockPos(pTag, corePos);
        }
        super.saveAdditional(pTag, pRegistries);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        corePos = NBTHelper.readBlockPos(pTag);
        super.loadAdditional(pTag, pRegistries);
    }

    @Override
    public ItemStack onClone(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        return getCore().map(c -> c.onClone(state, target, level, pos, player)).orElse(ItemStack.EMPTY);
    }

    @Override
    public ItemInteractionResult onUse(Player player, InteractionHand hand) {
        return getCore().map(c -> c.onUse(player, hand)).orElse(ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION);
    }

    @Override
    public InteractionResult onUseWithoutItem(Player player) {
        return getCore().map(c -> c.onUseWithoutItem(player)).orElse(InteractionResult.PASS);
    }

    @Override
    public ItemInteractionResult onUseWithItem(Player player, ItemStack stack, InteractionHand hand) {
        return getCore().map(c -> c.onUseWithItem(player, stack, hand)).orElse(ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION);
    }

    @Override
    public void onBreak(@Nullable Player player) {
        getCore().ifPresent(c -> c.onBreak(player));
    }

    public Optional<MultiBlockCoreEntity> getCore() {
        if (corePos != null && level.getBlockEntity(corePos) instanceof MultiBlockCoreEntity core) {
            return Optional.of(core);
        }
        return Optional.empty();
    }
}