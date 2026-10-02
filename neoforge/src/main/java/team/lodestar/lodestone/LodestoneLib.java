package team.lodestar.lodestone;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import org.apache.logging.log4j.Logger;
import team.lodestar.lodestone.compability.CuriosCompat;
import team.lodestar.lodestone.internal.LodestoneCommon;
import team.lodestar.lodestone.internal.network.LodestoneNetworking;
import team.lodestar.lodestone.neoforge.NeoForgeNetworkTransport;
import team.lodestar.lodestone.neoforge.NeoForgeParticleRegistrar;
import team.lodestar.lodestone.registry.common.LodestoneAttachmentTypes;
import team.lodestar.lodestone.registry.common.LodestoneBlockEntities;
import team.lodestar.lodestone.registry.common.LodestoneCommandArgumentTypes;
import team.lodestar.lodestone.registry.common.LodestoneEnchantmentComponents;
import team.lodestar.lodestone.registry.common.LodestonePlacementFillers;
import team.lodestar.lodestone.registry.common.LodestoneWorldEventTypes;
import team.lodestar.lodestone.registry.common.particle.LodestoneParticleTypes;
import team.lodestar.lodestone.events.ModEvents;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.api.distmarker.Dist;
import team.lodestar.lodestone.neoforge.LodestoneNeoForgeWorldEventsClient;
import team.lodestar.lodestone.internal.worldevent.WorldEventStorageAccess;
import team.lodestar.lodestone.internal.worldevent.WorldEventCommandContext;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import team.lodestar.lodestone.internal.worldevent.WorldEventCallbackAccess;
import team.lodestar.lodestone.internal.worldevent.NeoForgeWorldEventStorage;
import team.lodestar.lodestone.internal.worldevent.NeoForgeWorldEventCallbacks;
public class LodestoneLib {
    public static final Logger LOGGER = LodestoneCommon.LOGGER;
    public static final String LODESTONE = LodestoneCommon.LODESTONE;
    public static final RandomSource RANDOM = LodestoneCommon.RANDOM;

    public LodestoneLib(IEventBus modEventBus, ModContainer modContainer) {
        WorldEventCommandContext.serverSupplier(ServerLifecycleHooks::getCurrentServer);
        WorldEventStorageAccess.install(new NeoForgeWorldEventStorage());
        WorldEventCallbackAccess.install(new NeoForgeWorldEventCallbacks());
        if (FMLEnvironment.dist == Dist.CLIENT) {
            LodestoneNeoForgeWorldEventsClient.install();
        }
        LodestoneNetworking.install(new NeoForgeNetworkTransport());
        LodestoneCommon.init(new NeoForgeParticleRegistrar());
        LodestoneParticleTypes.PARTICLES.register(modEventBus);
        LodestonePlacementFillers.MODIFIERS.register(modEventBus);
        LodestoneAttachmentTypes.ATTACHMENT_TYPES.register(modEventBus);
        LodestoneBlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus);
        LodestoneWorldEventTypes.WORLD_EVENT_TYPES.register(modEventBus);
        LodestoneEnchantmentComponents.ENCHANTMENT_COMPONENTS.register(modEventBus);
        LodestoneCommandArgumentTypes.register(modEventBus);
        modEventBus.addListener(ModEvents::registerCommon);
        CuriosCompat.init();
    }

    public static ResourceLocation lodestonePath(String path) {
        return LodestoneCommon.lodestonePath(path);
    }
}
