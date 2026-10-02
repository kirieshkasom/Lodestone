package team.lodestar.lodestone.registry.common;

import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import team.lodestar.lodestone.modules.toolkit.worldgen.filter.ChancePlacementFilter;
import team.lodestar.lodestone.modules.toolkit.worldgen.filter.DimensionPlacementFilter;
import net.minecraft.core.registries.BuiltInRegistries;
import team.lodestar.lodestone.fabric.FabricRegistryRegistrar;
import team.lodestar.lodestone.internal.registration.RegistryRegistrar;
import team.lodestar.lodestone.internal.registration.LodestonePlacements;
import java.util.function.Supplier;

public class LodestonePlacementFillers {
    public static final RegistryRegistrar<PlacementModifierType<?>> MODIFIERS = new FabricRegistryRegistrar<>(BuiltInRegistries.PLACEMENT_MODIFIER_TYPE);

    public static final Supplier<PlacementModifierType<ChancePlacementFilter>> CHANCE = LodestonePlacements.CHANCE;
    public static final Supplier<PlacementModifierType<DimensionPlacementFilter>> DIMENSION = LodestonePlacements.DIMENSION;
}
