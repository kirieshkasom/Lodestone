package team.lodestar.lodestone.internal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import team.lodestar.lodestone.modules.toolkit.command.worldevent.DevWorldSetupCommand;
import team.lodestar.lodestone.modules.toolkit.command.worldevent.RemoveActiveWorldEventsCommand;
import team.lodestar.lodestone.modules.toolkit.command.worldevent.ListActiveWorldEventsCommand;
import team.lodestar.lodestone.modules.toolkit.command.worldevent.GetDataWorldEventCommand;
import team.lodestar.lodestone.modules.toolkit.command.worldevent.FreezeActiveWorldEventsCommand;
import team.lodestar.lodestone.modules.toolkit.command.worldevent.UnfreezeActiveWorldEventsCommand;
import team.lodestar.lodestone.modules.toolkit.command.worldevent.CreateWorldEventsCommand;

import static team.lodestar.lodestone.internal.LodestoneCommon.LODESTONE;

public class LodestoneCommandRegistration {

    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> cmd = dispatcher.register(Commands.literal("lode")
                        .then(DevWorldSetupCommand.register())
                        .then(Commands.literal("worldevent")
                                .then(RemoveActiveWorldEventsCommand.register())
                                .then(ListActiveWorldEventsCommand.register())
                                .then(GetDataWorldEventCommand.register())
                                .then(FreezeActiveWorldEventsCommand.register())
                                .then(UnfreezeActiveWorldEventsCommand.register())
                                .then(CreateWorldEventsCommand.register())
                        )
        );
        dispatcher.register(Commands.literal(LODESTONE)
                .redirect(cmd));
    }
}