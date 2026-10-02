package team.lodestar.lodestone.registry.common.particle;

import team.lodestar.lodestone.internal.registration.LodestoneParticles;
import team.lodestar.lodestone.modules.rendering.particle.standard.world.type.LodestoneItemCrumbsParticleType;
import team.lodestar.lodestone.modules.rendering.particle.standard.world.type.LodestoneTerrainParticleType;
import team.lodestar.lodestone.modules.rendering.particle.standard.world.type.LodestoneWorldParticleType;

import java.util.function.Supplier;
@SuppressWarnings("unused")
public class LodestoneParticleTypes {
    public static Supplier<LodestoneWorldParticleType> WISP_PARTICLE = LodestoneParticles.WISP_PARTICLE;
    public static Supplier<LodestoneWorldParticleType> SMOKE_PARTICLE = LodestoneParticles.SMOKE_PARTICLE;
    public static Supplier<LodestoneWorldParticleType> SPARKLE_PARTICLE = LodestoneParticles.SPARKLE_PARTICLE;
    public static Supplier<LodestoneWorldParticleType> TWINKLE_PARTICLE = LodestoneParticles.TWINKLE_PARTICLE;
    public static Supplier<LodestoneWorldParticleType> STAR_PARTICLE = LodestoneParticles.STAR_PARTICLE;
    public static Supplier<LodestoneWorldParticleType> SPARK_PARTICLE = LodestoneParticles.SPARK_PARTICLE;
    public static Supplier<LodestoneWorldParticleType> EXTRUDING_SPARK_PARTICLE = LodestoneParticles.EXTRUDING_SPARK_PARTICLE;
    public static Supplier<LodestoneWorldParticleType> THIN_EXTRUDING_SPARK_PARTICLE = LodestoneParticles.THIN_EXTRUDING_SPARK_PARTICLE;
    public static Supplier<LodestoneTerrainParticleType> TERRAIN_PARTICLE = LodestoneParticles.TERRAIN_PARTICLE;
    public static Supplier<LodestoneItemCrumbsParticleType> ITEM_PARTICLE = LodestoneParticles.ITEM_PARTICLE;
}
