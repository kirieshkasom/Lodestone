package team.lodestar.lodestone.mixin.modules.toolkit;

import net.minecraft.world.item.AxeItem;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import team.lodestar.lodestone.modules.toolkit.block.LodestoneLogBlock;

import java.util.Optional;

@Mixin(AxeItem.class)
public abstract class AxeItemMixin {
    @Inject(method = "getStripped", at = @At("HEAD"), cancellable = true)
    private void lodestone$stripLog(BlockState state, CallbackInfoReturnable<Optional<BlockState>> cir) {
        if (state.getBlock() instanceof LodestoneLogBlock log) {
            BlockState stripped = log.getStrippedState(state);
            if (stripped != null) {
                cir.setReturnValue(Optional.of(stripped));
            }
        }
    }
}
