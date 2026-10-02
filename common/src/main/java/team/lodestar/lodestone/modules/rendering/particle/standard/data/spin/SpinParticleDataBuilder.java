package team.lodestar.lodestone.modules.rendering.particle.standard.data.spin;

import net.minecraft.util.*;
import team.lodestar.lodestone.modules.core.easing.*;
import team.lodestar.lodestone.modules.rendering.particle.standard.data.*;

public class SpinParticleDataBuilder extends GenericParticleDataBuilder implements SpinParticleDataWrapper {
    protected float spinOffset;

    protected SpinParticleDataBuilder(float startingValue, float middleValue, float endingValue) {
        super(startingValue, middleValue, endingValue);
    }

    @Override
    public SpinParticleData unwrap() {
        return build();
    }

    public SpinParticleDataBuilder setSpinOffset(float spinOffset) {
        this.spinOffset = spinOffset;
        return this;
    }

    public SpinParticleDataBuilder randomSpinOffset(RandomSource random) {
        this.spinOffset = random.nextFloat() * 6.28f;
        return this;
    }

    @Override
    public SpinParticleDataBuilder setCoefficient(float coefficient) {
        return (SpinParticleDataBuilder) super.setCoefficient(coefficient);
    }

    @Override
    public SpinParticleDataBuilder setEasing(Easing easing) {
        return (SpinParticleDataBuilder) super.setEasing(easing);
    }

    @Override
    public SpinParticleDataBuilder setEasing(Easing easing, Easing middleToEndEasing) {
        return (SpinParticleDataBuilder) super.setEasing(easing, middleToEndEasing);
    }

    @Override
    public SpinParticleData build() {
        return new SpinParticleData(spinOffset, startingValue, middleValue, endingValue, coefficient, startToMiddleEasing, middleToEndEasing);
    }
}
