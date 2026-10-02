package team.lodestar.lodestone.fabric.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import team.lodestar.lodestone.modules.toolkit.creative_tab.CategorizedCreativeTabHandler;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenHighlightMixin {
    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderSlotHighlight(Lnet/minecraft/client/gui/GuiGraphics;III)V"))
    private void lodestone$disableSlotHighlight(GuiGraphics guiGraphics, int x, int y, int color, Operation<Void> original, @Local(ordinal = 0) Slot slot) {
        if (!CategorizedCreativeTabHandler.disableSlotHighlight(slot)) {
            original.call(guiGraphics, x, y, color);
        }
    }
}
