package team.lodestar.lodestone.internal.registration;

import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import team.lodestar.lodestone.internal.LodestoneCommon;
import team.lodestar.lodestone.modules.toolkit.worldgen.filter.ChancePlacementFilter;
import team.lodestar.lodestone.modules.toolkit.worldgen.filter.DimensionPlacementFilter;

public final class LodestonePlacements {
    public static final RegistryEntry<PlacementModifierType<ChancePlacementFilter>> CHANCE = new RegistryEntry<>(LodestoneCommon.lodestonePath("chance"), () -> () -> ChancePlacementFilter.CODEC);
    public static final RegistryEntry<PlacementModifierType<DimensionPlacementFilter>> DIMENSION = new RegistryEntry<>(LodestoneCommon.lodestonePath("dimension"), () -> () -> DimensionPlacementFilter.CODEC);

    private LodestonePlacements() {
    }

    public static void register(RegistryRegistrar<PlacementModifierType<?>> registrar) {
        registrar.register(CHANCE);
        registrar.register(DIMENSION);
    }
}
