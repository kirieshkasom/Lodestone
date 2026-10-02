package team.lodestar.lodestone.modules.rendering.particle.standard.data;

import net.minecraft.util.Mth;
import team.lodestar.lodestone.modules.core.easing.Easing;

@SuppressWarnings("unused")
public class GenericParticleData implements GenericParticleDataWrapper {
    public final float startingValue, middleValue, endingValue;
    public final float coefficient;
    public final Easing startingCurve, endingCurve;

    public float valueMultiplier = 1;
    public float coefficientMultiplier = 1;
    protected boolean locked;

    protected GenericParticleData(float startingValue, float middleValue, float endingValue, float coefficient, Easing startingCurve, Easing endingCurve) {
        this.startingValue = startingValue;
        this.middleValue = middleValue;
        this.endingValue = endingValue;
        this.coefficient = coefficient;
        this.startingCurve = startingCurve;
        this.endingCurve = endingCurve;
    }

    @Override
    public GenericParticleData unwrap() {
        return this;
    }

    /**
     * Creates a copy of the GenericParticleData instance.
     */
    public GenericParticleData copy() {
        return new GenericParticleData(startingValue, middleValue, endingValue, coefficient, startingCurve, endingCurve).overrideValueMultiplier(valueMultiplier).overrideCoefficientMultiplier(coefficientMultiplier);
    }

    /**
     * Bakes the data into a new GenericParticleData instance with the current multipliers baked into .
     */
    public GenericParticleData bake() {
        return new GenericParticleData(startingValue*valueMultiplier, middleValue*valueMultiplier, endingValue*valueMultiplier, coefficient*coefficientMultiplier, startingCurve, endingCurve);
    }

    /**
     * Locks the data, preventing any modifications to the value and coefficient.
     */
    public GenericParticleData lock() {
        locked = true;
        return this;
    }

    public GenericParticleData multiplyCoefficient(float coefficientMultiplier) {
        if (!locked) {
            this.coefficientMultiplier *= coefficientMultiplier;
        }
        return this;
    }

    public GenericParticleData overrideCoefficientMultiplier(float coefficientMultiplier) {
        if (!locked) {
            this.coefficientMultiplier = coefficientMultiplier;
        }
        return this;
    }

    public GenericParticleData multiplyValue(float valueMultiplier) {
        if (!locked) {
            this.valueMultiplier *= valueMultiplier;
        }
        return this;
    }

    public GenericParticleData overrideValueMultiplier(float valueMultiplier) {
        if (!locked) {
            this.valueMultiplier = valueMultiplier;
        }
        return this;
    }

    public float getCoefficient() {
        return coefficient * (coefficientMultiplier);
    }

    public float getValueMultiplier() {
        return valueMultiplier;
    }

    public boolean isTrinary() {
        return endingValue != -1;
    }

    public float getProgress(float age, float lifetime) {
        return Mth.clamp((age * getCoefficient()) / lifetime, 0, 1);
    }

    public float getValue(float age, float lifetime) {
        float progress = getProgress(age, lifetime);
        float result;
        if (isTrinary()) {
            if (progress >= 0.5f) {
                result = endingCurve.lerp((progress - 0.5f)*2, middleValue, endingValue);
            } else {
                result = startingCurve.lerp(progress*2, startingValue, middleValue);
            }
        } else {
            result = startingCurve.lerp(progress, startingValue, middleValue);
        }
        return result * getValueMultiplier();
    }

    public static GenericParticleDataBuilder create(float value) {
        return new GenericParticleDataBuilder(value, value, -1);
    }

    public static GenericParticleDataBuilder create(float startingValue, float endingValue) {
        return new GenericParticleDataBuilder(startingValue, endingValue, -1);
    }

    public static GenericParticleDataBuilder create(float startingValue, float middleValue, float endingValue) {
        return new GenericParticleDataBuilder(startingValue, middleValue, endingValue);
    }

    public static GenericParticleData constrictTransparency(GenericParticleData data) {
        float startingValue = Mth.clamp(data.startingValue, 0, 1);
        float middleValue = Mth.clamp(data.middleValue, 0, 1);
        float endingValue = data.endingValue == -1 ? -1 : Mth.clamp(data.endingValue, 0, 1);
        return new GenericParticleData(startingValue, middleValue, endingValue, data.coefficient, data.startingCurve, data.endingCurve);
    }
}