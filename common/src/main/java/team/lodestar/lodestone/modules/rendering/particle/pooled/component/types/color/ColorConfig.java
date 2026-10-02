package team.lodestar.lodestone.modules.rendering.particle.pooled.component.types.color;

import team.lodestar.lodestone.modules.core.easing.Easing;
import team.lodestar.lodestone.modules.rendering.particle.pooled.component.types.*;

import java.awt.*;
import java.util.Objects;

public class ColorConfig {
    protected ConstantOrLerp mode = ConstantOrLerp.CONSTANT;

    protected float r0 = 1.0f;
    protected float g0 = 1.0f;
    protected float b0 = 1.0f;
    protected float a0 = 1.0f;

    protected float r1 = 1.0f;
    protected float g1 = 1.0f;
    protected float b1 = 1.0f;
    protected float a1 = 1.0f;

    protected Easing easing;

    public ColorConfig constant(Color color) {
        return constant(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
    }

    public ColorConfig constant(int r, int g, int b, int a) {
        return constant(r / 255.0f, g / 255.0f, b / 255.0f, a / 255.0f);
    }

    public ColorConfig constant(float r, float g, float b, float a) {
        this.mode = ConstantOrLerp.CONSTANT;
        this.r0 = r;
        this.g0 = g;
        this.b0 = b;
        this.a0 = a;
        return this;
    }

    public ColorConfig interpolate(Color color0, Color color1, Easing easing) {
        return interpolate(color0.getRed() / 255.0f, color0.getGreen() / 255.0f, color0.getBlue() / 255.0f, color0.getAlpha() / 255.0f, color1.getRed() / 255.0f, color1.getGreen() / 255.0f, color1.getBlue() / 255.0f, color1.getAlpha() / 255.0f, easing);
    }

    public ColorConfig interpolate(int r0, int g0, int b0, int a0, int r1, int g1, int b1, int a1, Easing easing) {
        return interpolate(r0 / 255.0f, g0 / 255.0f, b0 / 255.0f, a0 / 255.0f, r1 / 255.0f, g1 / 255.0f, b1 / 255.0f, a1 / 255.0f, easing);
    }

    public ColorConfig interpolate(float r0, float g0, float b0, float a0, float r1, float g1, float b1, float a1, Easing easing) {
        this.mode = ConstantOrLerp.LERP;
        this.r0 = r0;
        this.g0 = g0;
        this.b0 = b0;
        this.a0 = a0;
        this.r1 = r1;
        this.g1 = g1;
        this.b1 = b1;
        this.a1 = a1;
        this.easing = easing;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ColorConfig that = (ColorConfig) o;
        return Float.compare(r0, that.r0) == 0 &&
                Float.compare(g0, that.g0) == 0 &&
                Float.compare(b0, that.b0) == 0 &&
                Float.compare(a0, that.a0) == 0 &&
                Float.compare(r1, that.r1) == 0 &&
                Float.compare(g1, that.g1) == 0 &&
                Float.compare(b1, that.b1) == 0 &&
                Float.compare(a1, that.a1) == 0 &&
                mode == that.mode &&
                Objects.equals(easing, that.easing);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mode, r0, g0, b0, a0, r1, g1, b1, a1, easing);
    }
}