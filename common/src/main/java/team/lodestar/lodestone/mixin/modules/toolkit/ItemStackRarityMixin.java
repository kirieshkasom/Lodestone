package team.lodestar.lodestone.mixin.modules.toolkit;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import team.lodestar.lodestone.internal.registration.LodestoneItemComponents;
import team.lodestar.lodestone.modules.toolkit.rarity.LodestoneRarity;

import java.util.ArrayList;
import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackRarityMixin {
    @Inject(method = "getRarity", at = @At("RETURN"), cancellable = true)
    private void lodestone$vanillaRarityTier(CallbackInfoReturnable<Rarity> callback) {
        ItemStack stack = (ItemStack) (Object) this;
        LodestoneRarity rarity = stack.get(LodestoneItemComponents.RARITY_STYLE.get());
        if (rarity == null) {
            return;
        }
        Rarity tier = rarity.vanillaRarity();
        if (stack.isEnchanted()) {
            tier = switch (tier) {
                case COMMON, UNCOMMON -> Rarity.RARE;
                case RARE -> Rarity.EPIC;
                default -> tier;
            };
        }
        callback.setReturnValue(tier);
    }

    @Inject(method = "getHoverName", at = @At("RETURN"), cancellable = true)
    private void applyLodestoneRarityStyle(CallbackInfoReturnable<Component> callback) {
        ItemStack stack = (ItemStack) (Object) this;
        LodestoneRarity rarity = stack.get(LodestoneItemComponents.RARITY_STYLE.get());
        if (rarity != null) {
            callback.setReturnValue(callback.getReturnValue().copy().withStyle(rarity.style()));
        }
    }

    @Inject(method = "getTooltipLines", at = @At("RETURN"), cancellable = true)
    private void applyLodestoneTooltipRarityStyle(Item.TooltipContext tooltipContext, Player player, TooltipFlag tooltipFlag, CallbackInfoReturnable<List<Component>> callback) {
        ItemStack stack = (ItemStack) (Object) this;
        LodestoneRarity rarity = stack.get(LodestoneItemComponents.RARITY_STYLE.get());
        if (rarity == null || callback.getReturnValue().isEmpty()) {
            return;
        }
        List<Component> tooltip = new ArrayList<>(callback.getReturnValue());
        tooltip.set(0, tooltip.get(0).copy().withStyle(rarity.style()));
        callback.setReturnValue(tooltip);
    }
}
