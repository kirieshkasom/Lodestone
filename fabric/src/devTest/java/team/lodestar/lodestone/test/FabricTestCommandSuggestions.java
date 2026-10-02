package team.lodestar.lodestone.test;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;

import java.util.List;

public final class FabricTestCommandSuggestions {

    private FabricTestCommandSuggestions() {
    }

    public static <S> boolean shouldSendToServer(String command, S source, CommandDispatcher<S> client, CommandDispatcher<S> combined) {
        return !complete(client.parse(command, source)) && complete(combined.parse(command, source));
    }

    private static <S> boolean complete(ParseResults<S> parse) {
        return !parse.getReader().canRead() && parse.getContext().getLastChild().getCommand() != null;
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
        for (String name : List.of("particle", "tests")) {
            CommandNode<S> branch = clientRoot.getChild(name);
            if (branch != null) {
                targetRoot.addChild(copySuggestions(branch));
            }
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
