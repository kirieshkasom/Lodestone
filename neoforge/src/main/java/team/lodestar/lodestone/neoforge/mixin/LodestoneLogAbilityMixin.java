package team.lodestar.lodestone.neoforge.mixin;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.extensions.IBlockExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import team.lodestar.lodestone.modules.toolkit.block.LodestoneLogBlock;

@Mixin(IBlockExtension.class)
public interface LodestoneLogAbilityMixin {
    @Inject(method = "getToolModifiedState", at = @At("HEAD"), cancellable = true)
    private void lodestone$stripLog(BlockState state, UseOnContext context, ItemAbility ability, boolean simulate, CallbackInfoReturnable<BlockState> cir) {
        if ((Object) this instanceof LodestoneLogBlock log && ability == ItemAbilities.AXE_STRIP && context.getItemInHand().canPerformAction(ability)) {
            BlockState stripped = log.getStrippedState(state);
            if (stripped != null) {
                cir.setReturnValue(stripped);
            }
        }
    }
}
