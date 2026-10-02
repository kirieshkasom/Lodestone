package team.lodestar.lodestone.registry.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import team.lodestar.lodestone.modules.toolkit.command.worldevent.*;

import static team.lodestar.lodestone.LodestoneLib.LODESTONE;

@EventBusSubscriber
public class LodestoneClientCommands {

    @SubscribeEvent
    public static void registerCommands(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        LiteralCommandNode<CommandSourceStack> cmd = dispatcher.register(Commands.literal("lodec")
                        .then(ParticleDebugCommand.register())
        );
        dispatcher.register(Commands.literal(LODESTONE + "c")
                .redirect(cmd));
    }
}