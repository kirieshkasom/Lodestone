package team.lodestar.lodestone.events;

import team.lodestar.lodestone.internal.registration.LodestoneParticles;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import team.lodestar.lodestone.handlers.screenparticle.ParticleEmitterHandler;
import team.lodestar.lodestone.registry.common.particle.*;
import team.lodestar.lodestone.modules.rendering.particle.standard.world.type.LodestoneItemCrumbsParticleType;
import team.lodestar.lodestone.modules.rendering.particle.standard.world.type.LodestoneTerrainParticleType;
import team.lodestar.lodestone.modules.rendering.particle.standard.world.type.LodestoneWorldParticleType;

@EventBusSubscriber(value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerParticleFactory(RegisterParticleProvidersEvent event) {
        registerParticleProviders(event);
        LodestoneScreenParticleTypes.registerParticleFactory(event);
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        ParticleEmitterHandler.registerParticleEmitters(event);
    }

    private static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(LodestoneParticles.WISP_PARTICLE.get(), LodestoneWorldParticleType.Factory::new);
        event.registerSpriteSet(LodestoneParticles.SMOKE_PARTICLE.get(), LodestoneWorldParticleType.Factory::new);
        event.registerSpriteSet(LodestoneParticles.SPARKLE_PARTICLE.get(), LodestoneWorldParticleType.Factory::new);
        event.registerSpriteSet(LodestoneParticles.TWINKLE_PARTICLE.get(), LodestoneWorldParticleType.Factory::new);
        event.registerSpriteSet(LodestoneParticles.STAR_PARTICLE.get(), LodestoneWorldParticleType.Factory::new);
        event.registerSpriteSet(LodestoneParticles.SPARK_PARTICLE.get(), LodestoneWorldParticleType.Factory::new);
        event.registerSpriteSet(LodestoneParticles.EXTRUDING_SPARK_PARTICLE.get(), LodestoneWorldParticleType.Factory::new);
        event.registerSpriteSet(LodestoneParticles.THIN_EXTRUDING_SPARK_PARTICLE.get(), LodestoneWorldParticleType.Factory::new);


        event.registerSpriteSet(LodestoneParticles.TERRAIN_PARTICLE.get(), s -> new LodestoneTerrainParticleType.Factory());
        event.registerSpriteSet(LodestoneParticles.ITEM_PARTICLE.get(), s -> new LodestoneItemCrumbsParticleType.Factory());
    }
}