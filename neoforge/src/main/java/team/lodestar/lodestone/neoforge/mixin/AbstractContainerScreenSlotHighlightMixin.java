package team.lodestar.lodestone.neoforge.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.lodestar.lodestone.modules.toolkit.creative_tab.CategorizedCreativeTabHandler;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenSlotHighlightMixin {
    @Inject(method = "renderSlotHighlight(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/inventory/Slot;IIF)V", at = @At("HEAD"), cancellable = true)
    private void lodestone$disableSlotHighlight(GuiGraphics guiGraphics, Slot slot, int mouseX, int mouseY, float partialTicks, CallbackInfo callback) {
        if (CategorizedCreativeTabHandler.disableSlotHighlight(slot)) {
            callback.cancel();
        }
    }
}
