package team.lodestar.lodestone.test.mixin;

import team.lodestar.lodestone.test.FabricTestCommandSuggestions;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.fabricmc.fabric.impl.command.client.ClientCommandInternals", remap = false)
public abstract class FabricTestCommandMixin {
    @Inject(method = "addCommands", at = @At("TAIL"), remap = false)
    private static void lodestoneTests$mergeSuggestions(CommandDispatcher<FabricClientCommandSource> target, FabricClientCommandSource source, CallbackInfo ci) {
        FabricTestCommandSuggestions.merge(target, ClientCommandManager.getActiveDispatcher());
    }

    @SuppressWarnings("unchecked")
    @Inject(method = "executeCommand", at = @At("HEAD"), cancellable = true, remap = false)
    private static void lodestoneTests$routeServerCommand(String command, CallbackInfoReturnable<Boolean> callback) {
        if (!command.startsWith("lode ") && !command.startsWith("lodestone ")) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getConnection() == null) {
            return;
        }
        FabricClientCommandSource source = (FabricClientCommandSource) minecraft.getConnection().getSuggestionsProvider();
        CommandDispatcher<FabricClientCommandSource> client = ClientCommandManager.getActiveDispatcher();
        CommandDispatcher<FabricClientCommandSource> combined = (CommandDispatcher<FabricClientCommandSource>) (CommandDispatcher<?>) minecraft.getConnection().getCommands();
        if (client != null && FabricTestCommandSuggestions.shouldSendToServer(command, source, client, combined)) {
            callback.setReturnValue(false);
        }
    }
}
