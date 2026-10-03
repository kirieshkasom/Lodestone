package team.lodestar.lodestone.fabric.mixin.gameplay;

import net.minecraft.world.item.CreativeModeTabs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.lodestar.lodestone.fabric.FabricCreativeTabOrdering;

@Mixin(value = CreativeModeTabs.class, priority = 900)
public class CreativeTabOrderingMixin {
    @Inject(method = "buildAllTabContents", at = @At("TAIL"))
    private static void lodestone$tabOrder(CallbackInfo callback) {
        FabricCreativeTabOrdering.apply();
    }
}
