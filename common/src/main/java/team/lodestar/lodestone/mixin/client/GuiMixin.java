package team.lodestar.lodestone.mixin.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import org.spongepowered.asm.mixin.Mixin;
import team.lodestar.lodestone.handlers.screenparticle.ScreenParticleHandler;

@Mixin(Gui.class)
public class GuiMixin {
    @WrapMethod(method = {"renderHotbar", "renderItemHotbar"}, require = 0)
    private void lodestone$renderHotbar(GuiGraphics guiGraphics, DeltaTracker deltaTracker, Operation<Void> original) {
        boolean wasRenderingHotbar = ScreenParticleHandler.renderingHotbar;
        ScreenParticleHandler.renderingHotbar = true;
        try {
            original.call(guiGraphics, deltaTracker);
        } finally {
            ScreenParticleHandler.renderingHotbar = wasRenderingHotbar;
        }
    }
}
