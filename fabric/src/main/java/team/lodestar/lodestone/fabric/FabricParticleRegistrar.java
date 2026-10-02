package team.lodestar.lodestone.fabric;

import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import team.lodestar.lodestone.internal.registration.ParticleRegistrar;

import java.util.function.Supplier;

public final class FabricParticleRegistrar implements ParticleRegistrar {
    @Override
    public <T extends ParticleType<?>> Supplier<T> register(ResourceLocation id, Supplier<T> factory) {
        T particle = Registry.register(BuiltInRegistries.PARTICLE_TYPE, id, factory.get());
        return () -> particle;
    }
}
