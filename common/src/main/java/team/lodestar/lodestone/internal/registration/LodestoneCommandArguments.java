package team.lodestar.lodestone.internal.registration;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import team.lodestar.lodestone.internal.LodestoneCommon;
import team.lodestar.lodestone.modules.toolkit.command.arguments.WorldEventInstanceArgument;
import team.lodestar.lodestone.modules.toolkit.command.arguments.WorldEventTypeArgument;

public final class LodestoneCommandArguments {
    public static final RegistryEntry<ArgumentTypeInfo<WorldEventTypeArgument, ?>> WORLD_EVENT_TYPE_ARG = new RegistryEntry<>(LodestoneCommon.lodestonePath("world_event_type_arg"), () -> SingletonArgumentInfo.contextFree(WorldEventTypeArgument::worldEventType));
    public static final RegistryEntry<ArgumentTypeInfo<WorldEventInstanceArgument, ?>> WORLD_EVENT_INSTANCE_ARG = new RegistryEntry<>(LodestoneCommon.lodestonePath("world_event_instance_arg"), () -> SingletonArgumentInfo.contextFree(WorldEventInstanceArgument::worldEventInstance));

    private LodestoneCommandArguments() {
    }

    public static void register(RegistryRegistrar<ArgumentTypeInfo<?, ?>> registrar) {
        registrar.register(WORLD_EVENT_TYPE_ARG);
        registrar.register(WORLD_EVENT_INSTANCE_ARG);
    }

}
