package team.lodestar.lodestone.neoforge.mixin;

import net.neoforged.neoforge.common.extensions.IItemExtension;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import team.lodestar.lodestone.modules.toolkit.item.LodestoneCombatItem;

@Mixin(IItemExtension.class)
public interface LodestoneToolAbilityMixin {
    @Inject(method = "canPerformAction", at = @At("HEAD"), cancellable = true)
    private void lodestone$swordAbility(ItemStack stack, ItemAbility ability, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof LodestoneCombatItem) {
            cir.setReturnValue(ability == ItemAbilities.SWORD_DIG);
        }
    }
}
