package team.lodestar.lodestone.modules.rendering.particle.standard.data;

import team.lodestar.lodestone.modules.core.easing.Easing;

public class GenericParticleDataBuilder implements GenericParticleDataWrapper {
    protected float startingValue, middleValue, endingValue;
    protected float coefficient = 1f;
    protected Easing startToMiddleEasing = Easing.LINEAR, middleToEndEasing = Easing.LINEAR;

    protected GenericParticleDataBuilder(float startingValue, float middleValue, float endingValue) {
        this.startingValue = startingValue;
        this.middleValue = middleValue;
        this.endingValue = endingValue;
    }

    @Override
    public GenericParticleData unwrap() {
        return build();
    }

    public GenericParticleDataBuilder setCoefficient(float coefficient) {
        this.coefficient = coefficient;
        return this;
    }

    public GenericParticleDataBuilder setEasing(Easing easing) {
        this.startToMiddleEasing = easing;
        return this;
    }

    public GenericParticleDataBuilder setEasing(Easing easing, Easing middleToEndEasing) {
        this.startToMiddleEasing = easing;
        this.middleToEndEasing = easing;
        return this;
    }

    public GenericParticleData build() {
        return new GenericParticleData(startingValue, middleValue, endingValue, coefficient, startToMiddleEasing, middleToEndEasing);
    }
}
