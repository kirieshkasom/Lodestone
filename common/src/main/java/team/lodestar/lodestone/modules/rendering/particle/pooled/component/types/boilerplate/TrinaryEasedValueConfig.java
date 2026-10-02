package team.lodestar.lodestone.modules.rendering.particle.pooled.component.types.boilerplate;

import team.lodestar.lodestone.modules.core.easing.*;
import team.lodestar.lodestone.modules.rendering.particle.pooled.component.types.*;

import java.util.*;

public class TrinaryEasedValueConfig implements ITrinaryConfig {
    protected ConstantLerpOrDoubleLerp mode = ConstantLerpOrDoubleLerp.CONSTANT;

    protected float s0 = 1.0f;
    protected float s1 = 1.0f;
    protected float s2 = 1.0f;

    protected Easing easing0;
    protected Easing easing1;

    public TrinaryEasedValueConfig constant(float scale) {
        this.mode = ConstantLerpOrDoubleLerp.CONSTANT;
        this.s0 = scale;
        this.s1 = scale;
        this.s2 = scale;
        return this;
    }

    public TrinaryEasedValueConfig interpolate(float s0, float s1, Easing easing) {
        this.mode = ConstantLerpOrDoubleLerp.LERP;
        this.s0 = s0;
        this.s1 = s1;
        this.easing0 = easing;
        return this;
    }

    public TrinaryEasedValueConfig interpolate(float s0, float s1, float s2, Easing easing0, Easing easing1) {
        this.mode = ConstantLerpOrDoubleLerp.DOUBLE_LERP;
        this.s0 = s0;
        this.s1 = s1;
        this.s2 = s2;
        this.easing0 = easing0;
        this.easing1 = easing1;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TrinaryEasedValueConfig that = (TrinaryEasedValueConfig) o;
        return Float.compare(s0, that.s0) == 0 &&
                Float.compare(s1, that.s1) == 0 &&
                Float.compare(s2, that.s2) == 0 &&
                mode == that.mode &&
                Objects.equals(easing0, that.easing0) &&
                Objects.equals(easing1, that.easing1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mode, s0, s1, s2, easing0, easing1);
    }

    @Override
    public ConstantLerpOrDoubleLerp getMode() {
        return mode;
    }

    @Override
    public float[] getValues() {
        return new float[]{s0, s1, s2};
    }

    @Override
    public Easing[] getEasings() {
        return new Easing[]{easing0, easing1};
    }
}