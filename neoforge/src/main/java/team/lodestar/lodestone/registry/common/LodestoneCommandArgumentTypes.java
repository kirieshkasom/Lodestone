package team.lodestar.lodestone.registry.common;

import team.lodestar.lodestone.internal.registration.LodestoneCommandArguments;
import team.lodestar.lodestone.neoforge.NeoForgeRegistryRegistrar;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import team.lodestar.lodestone.internal.LodestoneCommon;
import team.lodestar.lodestone.modules.toolkit.command.arguments.WorldEventInstanceArgument;
import team.lodestar.lodestone.modules.toolkit.command.arguments.WorldEventTypeArgument;

import java.util.function.Supplier;

public class LodestoneCommandArgumentTypes {
    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = DeferredRegister.create(BuiltInRegistries.COMMAND_ARGUMENT_TYPE, LodestoneCommon.LODESTONE);

    public static final Supplier<ArgumentTypeInfo<WorldEventTypeArgument,?>> WORLD_EVENT_TYPE_ARG = new NeoForgeRegistryRegistrar<>(COMMAND_ARGUMENT_TYPES).register(LodestoneCommandArguments.WORLD_EVENT_TYPE_ARG);
    public static final Supplier<ArgumentTypeInfo<WorldEventInstanceArgument,?>> WORLD_EVENT_INSTANCE_ARG = new NeoForgeRegistryRegistrar<>(COMMAND_ARGUMENT_TYPES).register(LodestoneCommandArguments.WORLD_EVENT_INSTANCE_ARG);



    public static void registerArgumentTypes() {
        ArgumentTypeInfos.registerByClass(WorldEventTypeArgument.class, WORLD_EVENT_TYPE_ARG.get());
        ArgumentTypeInfos.registerByClass(WorldEventInstanceArgument.class, WORLD_EVENT_INSTANCE_ARG.get());
    }
    public static void register(IEventBus modEventBus) {
        COMMAND_ARGUMENT_TYPES.register(modEventBus);
    }
}
