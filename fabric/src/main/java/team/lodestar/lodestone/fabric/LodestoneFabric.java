package team.lodestar.lodestone.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.server.level.ServerPlayer;
import team.lodestar.lodestone.internal.worldevent.WorldEventStorageAccess;
import team.lodestar.lodestone.internal.worldevent.WorldEventCommandContext;
import team.lodestar.lodestone.internal.worldevent.WorldEventCallbackAccess;
import team.lodestar.lodestone.internal.worldevent.FabricWorldEventStorage;
import team.lodestar.lodestone.registry.common.LodestoneWorldEventTypes;
import team.lodestar.lodestone.modules.toolkit.worldevent.WorldEventHandler;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import team.lodestar.lodestone.internal.LodestoneCommandRegistration;
import team.lodestar.lodestone.internal.LodestoneCommon;
import team.lodestar.lodestone.internal.network.LodestoneNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import team.lodestar.lodestone.registry.common.LodestoneNetworkPayloads;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.core.registries.BuiltInRegistries;
import team.lodestar.lodestone.modules.toolkit.reload_listener.ReloadRegistryLookup;
import team.lodestar.lodestone.internal.registration.LodestoneBlockEntityTypes;
import team.lodestar.lodestone.internal.registration.LodestoneEnchantmentEffects;
import team.lodestar.lodestone.internal.registration.LodestonePlacements;
import team.lodestar.lodestone.registry.common.LodestoneBlockEntities;
import team.lodestar.lodestone.registry.common.LodestoneCommandArgumentTypes;
import team.lodestar.lodestone.registry.common.LodestoneEnchantmentComponents;
import team.lodestar.lodestone.registry.common.LodestonePlacementFillers;

public final class LodestoneFabric implements ModInitializer {
    private static volatile MinecraftServer server;
    static final FabricWorldEventStorage WORLD_EVENT_STORAGE = new FabricWorldEventStorage();

    @Override
    public void onInitialize() {
        team.lodestar.lodestone.fabric.inventory.FabricInventoryAdapter.register();
        FabricCreativeTabEvents.register();
        FabricCategorizedCreativeTabFactory.install();
        team.lodestar.lodestone.internal.registration.LodestoneItemComponents.register(new FabricRegistryRegistrar<>(BuiltInRegistries.DATA_COMPONENT_TYPE));
        ReloadRegistryLookup.install(FabricReloadRegistryLookup::lookup);
        WorldEventCommandContext.serverSupplier(() -> server);
        WorldEventStorageAccess.install(WORLD_EVENT_STORAGE);
        WorldEventCallbackAccess.install(new FabricWorldEventCallbacks());
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            LodestoneFabricWorldEventsClient.install();
        }
        LodestoneWorldEventTypes.getEventTypes();
        ServerTickEvents.END_WORLD_TICK.register(WorldEventHandler::worldTick);
        ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
            if (entity instanceof ServerPlayer player) {
                WorldEventHandler.playerJoin(player);
            }
        });
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
