package team.lodestar.lodestone.modules.rendering.particle.standard.data.spin;

import net.minecraft.util.RandomSource;
import team.lodestar.lodestone.modules.core.easing.Easing;
import team.lodestar.lodestone.modules.rendering.particle.standard.data.GenericParticleData;

public class SpinParticleData extends GenericParticleData implements SpinParticleDataWrapper {

    public final float spinOffset;

    protected SpinParticleData(float spinOffset, float startingValue, float middleValue, float endingValue, float coefficient, Easing startToMiddleEasing, Easing middleToEndEasing) {
        super(startingValue, middleValue, endingValue, coefficient, startToMiddleEasing, middleToEndEasing);
        this.spinOffset = spinOffset;
    }

    @Override
    public SpinParticleData unwrap() {
        return this;
    }

    @Override
    public SpinParticleData copy() {
        return new SpinParticleData(spinOffset, startingValue, middleValue, endingValue, coefficient, startingCurve, endingCurve).overrideValueMultiplier(valueMultiplier).overrideCoefficientMultiplier(coefficientMultiplier);
    }

    @Override
    public SpinParticleData bake() {
        return new SpinParticleData(spinOffset, startingValue*valueMultiplier, middleValue*valueMultiplier, endingValue*valueMultiplier, coefficient*coefficientMultiplier, startingCurve, endingCurve);
    }

    @Override
    public SpinParticleData overrideValueMultiplier(float valueMultiplier) {
        return (SpinParticleData) super.overrideValueMultiplier(valueMultiplier);
    }

    @Override
    public SpinParticleData overrideCoefficientMultiplier(float coefficientMultiplier) {
        return (SpinParticleData) super.overrideCoefficientMultiplier(coefficientMultiplier);
    }

    @Override
    public SpinParticleData lock() {
        return (SpinParticleData) super.lock();
    }

    public static SpinParticleDataBuilder create(float value) {
        return new SpinParticleDataBuilder(value, value, -1);
    }

    public static SpinParticleDataBuilder create(float startingValue, float endingValue) {
        return new SpinParticleDataBuilder(startingValue, endingValue, -1);
    }

    public static SpinParticleDataBuilder create(float startingValue, float middleValue, float endingValue) {
        return new SpinParticleDataBuilder(startingValue, middleValue, endingValue);
    }

    public static SpinParticleDataBuilder createRandomDirection(RandomSource random, float value) {
        value *= random.nextBoolean() ? 1 : -1;
        return new SpinParticleDataBuilder(value, value, -1);
    }

    public static SpinParticleDataBuilder createRandomDirection(RandomSource random, float startingValue, float endingValue) {
        final int direction = random.nextBoolean() ? 1 : -1;
        startingValue *= direction;
        endingValue *= direction;
        return new SpinParticleDataBuilder(startingValue, endingValue, -1);
    }

    public static SpinParticleDataBuilder createRandomDirection(RandomSource random, float startingValue, float middleValue, float endingValue) {
        final int direction = random.nextBoolean() ? 1 : -1;
        startingValue *= direction;
        middleValue *= direction;
        endingValue *= direction;
        return new SpinParticleDataBuilder(startingValue, middleValue, endingValue);
    }
}