package team.lodestar.lodestone.internal.registration;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;
public interface ParticleRegistrar {
    <T extends ParticleType<?>> Supplier<T> register(ResourceLocation id, Supplier<T> factory);
}
