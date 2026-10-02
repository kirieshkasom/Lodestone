package team.lodestar.lodestone.mixin.modules.toolkit.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.lodestar.lodestone.modules.toolkit.creative_tab.CategorizedCreativeTabHandler;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {

    @Inject(method = "renderSlot", at = @At("HEAD"), cancellable = true)
    private void lodestone$modifySlotRendering(GuiGraphics guiGraphics, Slot slot, CallbackInfo ci) {
        if (CategorizedCreativeTabHandler.renderSlot(guiGraphics, slot)) {
            ci.cancel();
        }
    }

}
