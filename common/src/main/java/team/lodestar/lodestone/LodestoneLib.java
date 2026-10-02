package team.lodestar.lodestone;

import net.minecraft.resources.*;
import net.minecraft.util.*;
import net.neoforged.bus.api.*;
import net.neoforged.fml.*;
import net.neoforged.fml.common.*;
import org.apache.logging.log4j.*;
import team.lodestar.lodestone.compability.*;
import team.lodestar.lodestone.registry.common.*;
import team.lodestar.lodestone.registry.common.particle.*;

public class LodestoneLib {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String LODESTONE = "lodestone";
    public static final RandomSource RANDOM = RandomSource.create();

    public LodestoneLib(IEventBus modEventBus, ModContainer modContainer) {

        LodestoneParticleTypes.PARTICLES.register(modEventBus);
        LodestonePlacementFillers.MODIFIERS.register(modEventBus);
        LodestoneAttachmentTypes.ATTACHMENT_TYPES.register(modEventBus);
        LodestoneBlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus);
        LodestoneWorldEventTypes.WORLD_EVENT_TYPES.register(modEventBus);
        LodestoneEnchantmentComponents.ENCHANTMENT_COMPONENTS.register(modEventBus);
        LodestoneCommandArgumentTypes.register(modEventBus);

        CuriosCompat.init();
    }

    public static ResourceLocation lodestonePath(String path) {
        return ResourceLocation.fromNamespaceAndPath(LODESTONE, path);
    }
}