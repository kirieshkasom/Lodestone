package team.lodestar.lodestone.modules.toolkit.worldgen;

import net.minecraft.world.level.*;

@FunctionalInterface
public interface PlacementCondition {
    PlacementCondition CAN_SURVIVE = (level, entry) -> entry.blockState().canSurvive(level, entry.position());

    boolean canPlace(WorldGenLevel level, LodestoneWorldgenBuilderEntry entry);
}