package team.lodestar.lodestone.registry.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import team.lodestar.lodestone.modules.toolkit.command.worldevent.*;

import static team.lodestar.lodestone.internal.LodestoneCommon.LODESTONE;

public class LodestoneClientCommands {

    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> cmd = dispatcher.register(Commands.literal("lodec")
                        .then(ParticleDebugCommand.register())
        );
        dispatcher.register(Commands.literal(LODESTONE + "c")
                .redirect(cmd));
    }
}