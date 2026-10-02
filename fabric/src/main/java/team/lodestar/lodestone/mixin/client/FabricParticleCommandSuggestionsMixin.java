package team.lodestar.lodestone.mixin.client;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.lodestar.lodestone.fabric.FabricParticleCommandSuggestions;

@Mixin(targets = "net.fabricmc.fabric.impl.command.client.ClientCommandInternals", remap = false)
public abstract class FabricParticleCommandSuggestionsMixin {

    @Inject(method = "addCommands", at = @At("TAIL"), remap = false)
    private static void lodestone$mergeParticleSuggestions(CommandDispatcher<FabricClientCommandSource> target, FabricClientCommandSource source, CallbackInfo ci) {
        FabricParticleCommandSuggestions.merge(target, ClientCommandManager.getActiveDispatcher());
    }
}
