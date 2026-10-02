package team.lodestar.lodestone.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import team.lodestar.lodestone.internal.LodestoneCommandRegistration;
import team.lodestar.lodestone.internal.LodestoneCommon;
import team.lodestar.lodestone.internal.network.LodestoneNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import team.lodestar.lodestone.registry.common.LodestoneNetworkPayloads;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.core.registries.BuiltInRegistries;
import team.lodestar.lodestone.internal.registration.LodestoneBlockEntityTypes;
import team.lodestar.lodestone.internal.registration.LodestoneEnchantmentEffects;
import team.lodestar.lodestone.internal.registration.LodestonePlacements;
import team.lodestar.lodestone.registry.common.LodestoneBlockEntities;
import team.lodestar.lodestone.registry.common.LodestoneCommandArgumentTypes;
import team.lodestar.lodestone.registry.common.LodestoneEnchantmentComponents;
import team.lodestar.lodestone.registry.common.LodestonePlacementFillers;

public final class LodestoneFabric implements ModInitializer {
    private static volatile MinecraftServer server;

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register(startingServer -> server = startingServer);
        ServerLifecycleEvents.SERVER_STOPPED.register(stoppedServer -> server = null);
        LodestoneNetworking.install(new FabricNetworkTransport(() -> server));
        LodestoneNetworkPayloads.register();
        LodestoneCommon.init(new FabricParticleRegistrar());
        LodestonePlacements.register(LodestonePlacementFillers.MODIFIERS);
        LodestoneBlockEntityTypes.register(LodestoneBlockEntities.BLOCK_ENTITY_TYPES);
        RegistryEntryAddedCallback.event(BuiltInRegistries.BLOCK).register((rawId, id, block) -> LodestoneBlockEntityTypes.blockRegistered(block));
        LodestoneEnchantmentEffects.register(LodestoneEnchantmentComponents.ENCHANTMENT_COMPONENTS);
        LodestoneCommandArgumentTypes.registerArgumentTypes();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, selection) -> LodestoneCommandRegistration.registerCommands(dispatcher));
    }
}
