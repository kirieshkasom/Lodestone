package team.lodestar.lodestone.registry.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import team.lodestar.lodestone.modules.toolkit.command.worldevent.ParticleDebugCommand;

import java.util.function.BiConsumer;

import static team.lodestar.lodestone.internal.LodestoneCommon.LODESTONE;

public class LodestoneClientCommands {

    public static void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        registerCommands(dispatcher, (source, component) -> source.sendSuccess(() -> component, false), CommandSourceStack::sendFailure);
    }

    public static <S> void registerCommands(CommandDispatcher<S> dispatcher, BiConsumer<S, Component> success, BiConsumer<S, Component> failure) {
        LiteralCommandNode<S> command = dispatcher.register(LiteralArgumentBuilder.<S>literal("lodec")
                .then(ParticleDebugCommand.register(success, failure)));
        dispatcher.register(LiteralArgumentBuilder.<S>literal(LODESTONE + "c").redirect(command));
    }
}
