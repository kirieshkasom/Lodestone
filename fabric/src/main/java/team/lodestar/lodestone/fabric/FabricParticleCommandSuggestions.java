package team.lodestar.lodestone.fabric;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;

public final class FabricParticleCommandSuggestions {

    private FabricParticleCommandSuggestions() {
    }

    public static <S> void merge(CommandDispatcher<S> target, CommandDispatcher<S> client) {
        if (client == null) {
            return;
        }
        CommandNode<S> clientRoot = client.getRoot().getChild("lode");
        CommandNode<S> targetRoot = target.getRoot().getChild("lode");
        if (clientRoot == null || targetRoot == null) {
            return;
        }
        CommandNode<S> particles = clientRoot.getChild("particle");
        if (particles != null) {
            targetRoot.addChild(copySuggestions(particles));
        }
    }

    private static <S> CommandNode<S> copySuggestions(CommandNode<S> node) {
        ArgumentBuilder<S, ?> builder = node.createBuilder().requires(source -> true);
        if (builder.getCommand() != null) {
            builder.executes(context -> 0);
        }
        for (CommandNode<S> child : node.getChildren()) {
            builder.then(copySuggestions(child));
        }
        return builder.build();
    }
}
