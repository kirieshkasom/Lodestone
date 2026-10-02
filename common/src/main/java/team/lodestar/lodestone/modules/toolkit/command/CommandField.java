package team.lodestar.lodestone.modules.toolkit.command;

import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;

public interface CommandField<A, V> {
    String getName();

    RequiredArgumentBuilder<CommandSourceStack, A> getArgument();

    V getValue(com.mojang.brigadier.context.CommandContext<net.minecraft.commands.CommandSourceStack> ctx);
}
