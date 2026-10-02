package team.lodestar.lodestone.modules.rendering.particle.standard.screen;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.SpriteSet;
import team.lodestar.lodestone.modules.rendering.particle.standard.screen.base.ScreenParticle;

public class LodestoneScreenParticleType extends ScreenParticleType<ScreenParticleOptions> {

    public LodestoneScreenParticleType() {
        super();
    }

    public record Factory(SpriteSet sprite) implements ParticleProvider<ScreenParticleOptions> {

        @Override
        public ScreenParticle createParticle(ClientLevel pLevel, ScreenParticleOptions options, double x, double y, double pXSpeed, double pYSpeed) {
            return new LodestoneScreenParticle(pLevel, options, (ParticleEngine.MutableSpriteSet) sprite, x, y, pXSpeed, pYSpeed);
        }
    }
}