package team.lodestar.lodestone.modules.rendering.particle.standard.world.type;

import net.minecraft.client.multiplayer.*;
import net.minecraft.client.particle.*;
import team.lodestar.lodestone.modules.rendering.particle.standard.world.*;
import team.lodestar.lodestone.modules.rendering.particle.standard.world.options.*;

import javax.annotation.*;

public class LodestoneItemCrumbsParticleType extends AbstractLodestoneParticleType<LodestoneItemCrumbsParticleOptions> {

    public LodestoneItemCrumbsParticleType() {
        super();
    }

    public static class Factory implements ParticleProvider<LodestoneItemCrumbsParticleOptions> {

        public Factory() {
        }

        @Nullable
        @Override
        public Particle createParticle(LodestoneItemCrumbsParticleOptions data, ClientLevel world, double x, double y, double z, double mx, double my, double mz) {
            return new LodestoneItemCrumbParticle(world, data, x, y, z, mx, my, mz);
        }
    }
}