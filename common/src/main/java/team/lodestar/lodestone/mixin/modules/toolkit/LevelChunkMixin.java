package team.lodestar.lodestone.mixin.modules.toolkit;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import team.lodestar.lodestone.modules.toolkit.blockentity.LodestoneBlockEntity;
import team.lodestar.lodestone.modules.toolkit.blockentity.LodestoneBlockEntityType;

@Mixin(LevelChunk.class)
public class LevelChunkMixin {

    @SuppressWarnings("unchecked")
    @WrapOperation(method = "updateBlockEntityTicker", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getTicker(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/entity/BlockEntityType;)Lnet/minecraft/world/level/block/entity/BlockEntityTicker;"))
    private <T extends BlockEntity> BlockEntityTicker<T> lodestone$attachBlockEntityAwareTicker(BlockState state, Level level, BlockEntityType<T> blockEntityType, Operation<BlockEntityTicker<T>> original, T blockEntity) {
        if (blockEntityType instanceof LodestoneBlockEntityType<?> lodestoneType) {
            if (blockEntity instanceof LodestoneBlockEntity entity) {
                return (BlockEntityTicker<T>) lodestoneType.getBlockEntityAwareTicker(level, state, entity);
            }
            return null;
        }
        return original.call(state, level, blockEntityType);
    }
}
