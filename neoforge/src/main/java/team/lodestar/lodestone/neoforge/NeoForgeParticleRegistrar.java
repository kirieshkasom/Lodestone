package team.lodestar.lodestone.neoforge;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import team.lodestar.lodestone.internal.LodestoneCommon;
import team.lodestar.lodestone.internal.registration.ParticleRegistrar;
import team.lodestar.lodestone.registry.common.particle.LodestoneParticleTypes;

import java.util.function.Supplier;

public final class NeoForgeParticleRegistrar implements ParticleRegistrar {
    @Override
    public <T extends ParticleType<?>> Supplier<T> register(ResourceLocation id, Supplier<T> factory) {
        if (!LodestoneCommon.LODESTONE.equals(id.getNamespace())) {
            throw new IllegalArgumentException("Unexpected particle namespace: " + id);
        }
        return LodestoneParticleTypes.PARTICLES.register(id.getPath(), factory);
    }
}
