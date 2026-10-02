package team.lodestar.lodestone.registry.common;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import team.lodestar.lodestone.modules.toolkit.command.arguments.WorldEventTypeArgument;
import team.lodestar.lodestone.modules.toolkit.command.arguments.WorldEventInstanceArgument;
import net.minecraft.core.registries.BuiltInRegistries;
import team.lodestar.lodestone.fabric.FabricRegistryRegistrar;
import team.lodestar.lodestone.internal.registration.RegistryRegistrar;
import team.lodestar.lodestone.internal.registration.LodestoneCommandArguments;
import java.util.function.Supplier;

public class LodestoneCommandArgumentTypes {
    public static final RegistryRegistrar<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = new FabricRegistryRegistrar<>(BuiltInRegistries.COMMAND_ARGUMENT_TYPE);

    public static final Supplier<ArgumentTypeInfo<WorldEventTypeArgument, ?>> WORLD_EVENT_TYPE_ARG = LodestoneCommandArguments.WORLD_EVENT_TYPE_ARG;
    public static final Supplier<ArgumentTypeInfo<WorldEventInstanceArgument, ?>> WORLD_EVENT_INSTANCE_ARG = LodestoneCommandArguments.WORLD_EVENT_INSTANCE_ARG;

    public static void registerArgumentTypes() {
        LodestoneCommandArguments.WORLD_EVENT_TYPE_ARG.ensureUnbound();
        LodestoneCommandArguments.WORLD_EVENT_INSTANCE_ARG.ensureUnbound();
        ArgumentTypeInfo<WorldEventTypeArgument, ?> typeInfo = LodestoneCommandArguments.WORLD_EVENT_TYPE_ARG.factory().get();
        ArgumentTypeRegistry.registerArgumentType(LodestoneCommandArguments.WORLD_EVENT_TYPE_ARG.id(), WorldEventTypeArgument.class, typeInfo);
        LodestoneCommandArguments.WORLD_EVENT_TYPE_ARG.bind(() -> typeInfo);
        ArgumentTypeInfo<WorldEventInstanceArgument, ?> instanceInfo = LodestoneCommandArguments.WORLD_EVENT_INSTANCE_ARG.factory().get();
        ArgumentTypeRegistry.registerArgumentType(LodestoneCommandArguments.WORLD_EVENT_INSTANCE_ARG.id(), WorldEventInstanceArgument.class, instanceInfo);
        LodestoneCommandArguments.WORLD_EVENT_INSTANCE_ARG.bind(() -> instanceInfo);
    }
}
