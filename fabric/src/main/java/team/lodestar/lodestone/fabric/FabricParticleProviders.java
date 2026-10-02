package team.lodestar.lodestone.fabric;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import team.lodestar.lodestone.internal.registration.LodestoneParticles;
import team.lodestar.lodestone.modules.rendering.particle.standard.world.LodestoneParticleSpriteSet;
import team.lodestar.lodestone.modules.rendering.particle.standard.world.type.LodestoneWorldParticleType;
import team.lodestar.lodestone.modules.rendering.particle.standard.world.type.LodestoneTerrainParticleType;
import team.lodestar.lodestone.modules.rendering.particle.standard.world.type.LodestoneItemCrumbsParticleType;

public final class FabricParticleProviders {
    private FabricParticleProviders() {
    }

    public static void register() {
        ParticleFactoryRegistry registry = ParticleFactoryRegistry.getInstance();
        registry.register(LodestoneParticles.WISP_PARTICLE.get(), sprites -> new LodestoneWorldParticleType.Factory(new LodestoneParticleSpriteSet(sprites::getSprites)));
        registry.register(LodestoneParticles.SMOKE_PARTICLE.get(), sprites -> new LodestoneWorldParticleType.Factory(new LodestoneParticleSpriteSet(sprites::getSprites)));
        registry.register(LodestoneParticles.SPARKLE_PARTICLE.get(), sprites -> new LodestoneWorldParticleType.Factory(new LodestoneParticleSpriteSet(sprites::getSprites)));
        registry.register(LodestoneParticles.TWINKLE_PARTICLE.get(), sprites -> new LodestoneWorldParticleType.Factory(new LodestoneParticleSpriteSet(sprites::getSprites)));
        registry.register(LodestoneParticles.STAR_PARTICLE.get(), sprites -> new LodestoneWorldParticleType.Factory(new LodestoneParticleSpriteSet(sprites::getSprites)));
        registry.register(LodestoneParticles.SPARK_PARTICLE.get(), sprites -> new LodestoneWorldParticleType.Factory(new LodestoneParticleSpriteSet(sprites::getSprites)));
        registry.register(LodestoneParticles.EXTRUDING_SPARK_PARTICLE.get(), sprites -> new LodestoneWorldParticleType.Factory(new LodestoneParticleSpriteSet(sprites::getSprites)));
        registry.register(LodestoneParticles.THIN_EXTRUDING_SPARK_PARTICLE.get(), sprites -> new LodestoneWorldParticleType.Factory(new LodestoneParticleSpriteSet(sprites::getSprites)));
        registry.register(LodestoneParticles.TERRAIN_PARTICLE.get(), sprites -> new LodestoneTerrainParticleType.Factory());
        registry.register(LodestoneParticles.ITEM_PARTICLE.get(), sprites -> new LodestoneItemCrumbsParticleType.Factory());
    }
}
