package team.lodestar.lodestone.fabric.mixin.gameplay;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.lodestar.lodestone.handlers.ItemEventHandler;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemResponseTooltipMixin {
    @Inject(method = "addAttributeTooltips", at = @At("TAIL"))
    private void lodestone$attributeTooltip(Consumer<Component> tooltip, Player player, CallbackInfo ci) {
        ItemEventHandler.addAttributeTooltips(new ItemEventHandler.TooltipContext((ItemStack) (Object) this, tooltip));
    }
}
