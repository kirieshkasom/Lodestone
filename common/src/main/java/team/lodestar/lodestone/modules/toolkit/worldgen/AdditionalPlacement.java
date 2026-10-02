package team.lodestar.lodestone.modules.toolkit.worldgen;

import net.minecraft.world.level.*;

@FunctionalInterface
public interface AdditionalPlacement {
    void place(WorldGenLevel level, LodestoneWorldgenBuilderEntry entry);
}