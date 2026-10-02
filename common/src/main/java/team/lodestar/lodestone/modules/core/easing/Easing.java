package team.lodestar.lodestone.modules.core.easing;

import com.mojang.serialization.Codec;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

import java.util.HashMap;

@SuppressWarnings("unused")
public abstract class Easing {

    public static final Codec<Easing> CODEC = Codec.STRING.xmap(Easing::valueOf, e -> e.name);

    public static final HashMap<String, Easing> EASINGS = new HashMap<>();
    public final String name;

    public Easing(String name) {
        this.name = name;
        EASINGS.put(name, this);
    }

    public static Easing valueOf(String name) {
        return EASINGS.get(name);
    }

    public abstract double ease(double delta);

    public float ease(float delta) {
        return (float) ease((double) delta);
    }

    protected double pow(double delta, double exponent) {
        return Math.pow(delta, exponent);
    }

    protected double exponentOut(double delta, double exponent) {
        return 1 - pow(1 - delta, exponent);
    }

    protected double exponentInOut(double delta, double exponent) {
        return delta < 0.5
                ? pow(2 * delta, exponent) / 2
                : 1 - pow(2 * (1 - delta), exponent) / 2;
    }

    public double lerp(double delta, double min, double max) {
        var pct = ease(delta);
        return Mth.lerp(pct, min, max);
    }

    public float lerp(float delta, float min, float max) {
        var pct = ease(delta);
        return Mth.lerp(pct, min, max);
    }

    public int asWeighedRandom(RandomSource randomSource, int start, int middle, int end) {
        return asValueDistribution(randomSource.nextDouble(), start, middle, end);
    }

    public float asWeighedRandom(RandomSource randomSource, float start, float middle, float end) {
        return asValueDistribution(randomSource.nextDouble(), start, middle, end);
    }

    public double asWeighedRandom(RandomSource randomSource, double start, double middle, double end) {
        return asValueDistribution(randomSource.nextDouble(), start, middle, end);
    }

    public int asWeighedRandom(RandomSource randomSource, int start, int end) {
        return asValueDistribution(randomSource.nextDouble(), start, end);
    }

    public float asWeighedRandom(RandomSource randomSource, float start, float end) {
        return asValueDistribution(randomSource.nextDouble(), start, end);
    }

    public double asWeighedRandom(RandomSource randomSource, double start, double end) {
        return asValueDistribution(randomSource.nextDouble(), start, end);
    }

    public int asValueDistribution(double delta, int start, int end) {
        return asValueDistribution(delta, start, (start + end) / 2, end);
    }

    public float asValueDistribution(double delta, float start, float end) {
        return asValueDistribution(delta, start, (start + end) / 2f, end);
    }

    public double asValueDistribution(double delta, double start, double end) {
        return asValueDistribution(delta, start, (start + end) / 2f, end);
    }

    public int asValueDistribution(double delta, int start, int middle, int end) {
        return (int) Math.round(asValueDistribution(delta, (double) start, middle, end));
    }

    public float asValueDistribution(double delta, float start, float middle, float end) {
        return (float) asValueDistribution(delta, (double)start, middle, end);
    }

    public double asValueDistribution(double delta, double start, double middle, double end) {
        double offset = Math.abs(0.5 - delta) / 0.5;
        double easedOffset = ease(1 - offset);
        if (delta < 0.5) {
            return Mth.lerp(easedOffset, start, middle);
        } else {
            return Mth.lerp(1 - easedOffset, middle, end);
        }
    }

    public static final Easing LINEAR = new Easing("linear") {

        public double ease(double delta) {
            return delta;
        }
    };

    public static final Easing SINE_IN = new Easing("sineIn") {

        public double ease(double delta) {
            return (1 - Math.cos((delta * Mth.PI) / 2));
        }
    };

    public static final Easing SINE_OUT = new Easing("sineOut") {

        public double ease(double delta) {
            return Math.sin((delta * Mth.PI) / 2);
        }
    };

    public static final Easing SINE_IN_OUT = new Easing("sineInOut") {

        public double ease(double delta) {
            return (-(Math.cos(Mth.PI * delta) - 1) / 2);
        }
    };

    public static final Easing QUAD_IN = new Easing("quadIn") {

        public double ease(double delta) {
            return pow(delta, 2);
        }
    };

    public static final Easing QUAD_OUT = new Easing("quadOut") {

        public double ease(double delta) {
            return exponentOut(delta, 2);
        }
    };

    public static final Easing QUAD_IN_OUT = new Easing("quadInOut") {

        public double ease(double delta) {
            return exponentInOut(delta, 2);
        }
    };

    public static final Easing CUBIC_IN = new Easing("cubicIn") {

        public double ease(double delta) {
            return pow(delta, 3);
        }
    };

    public static final Easing CUBIC_OUT = new Easing("cubicOut") {

        public double ease(double delta) {
            return exponentOut(delta, 3);
        }
    };

    public static final Easing CUBIC_IN_OUT = new Easing("cubicInOut") {

        public double ease(double delta) {
            return exponentInOut(delta, 3);
        }
    };

    public static final Easing QUARTIC_IN = new Easing("quarticIn") {

        public double ease(double delta) {
            return pow(delta, 4);
        }
    };

    public static final Easing QUARTIC_OUT = new Easing("quarticOut") {

        public double ease(double delta) {
            return exponentOut(delta, 4);
        }
    };

    public static final Easing QUARTIC_IN_OUT = new Easing("quarticInOut") {

        public double ease(double delta) {
            return exponentInOut(delta, 4);
        }
    };

    public static final Easing QUINTIC_IN = new Easing("quinticIn") {

        public double ease(double delta) {
            return pow(delta, 5);
        }
    };

    public static final Easing QUINTIC_OUT = new Easing("quinticOut") {

        public double ease(double delta) {
            return exponentOut(delta, 5);
        }
    };

    public static final Easing QUINTIC_IN_OUT = new Easing("quinticInOut") {

        public double ease(double delta) {
            return exponentInOut(delta, 5);
        }
    };

    public static final Easing EXPO_IN = new Easing("expoIn") {
        public double ease(double delta) {
            return delta == 0
                    ? 0
                    : (double) pow(2, 10 * delta - 10);
        }
    };

    public static final Easing EXPO_OUT = new Easing("expoOut") {
        public double ease(double delta) {
            return delta == 1
                    ? 1
                    : (double) (1 - pow(2, -10 * delta));
        }
    };

    public static final Easing EXPO_IN_OUT = new Easing("expoInOut") {
        public double ease(double delta) {
            if (delta <= 0) {
                return 0;
            }
            if (delta >= 1) {
                return 1;
            }
            return delta < 0.5f
                    ? (double) pow(2, 20 * delta - 10) / 2f
                    : (double) (2 - pow(2, -20 * delta + 10)) / 2f;
        }
    };

    public static final Easing CIRC_IN = new Easing("circIn") {
        public double ease(double delta) {
            return (double) (1 - Math.sqrt(1 - pow(delta, 2)));
        }
    };

    public static final Easing CIRC_OUT = new Easing("circOut") {
        public double ease(double delta) {
            return (double) Math.sqrt(1 - pow(delta - 1, 2));
        }
    };

    public static final Easing CIRC_IN_OUT = new Easing("circInOut") {
        public double ease(double delta) {
            return delta < 0.5f
                    ? (double) (1 - Math.sqrt(1 - pow(2 * delta, 2))) / 2f
                    : (double) (Math.sqrt(1 - pow(-2 * delta + 2, 2)) + 1) / 2f;
        }
    };

    public static final Easing SMOOTH_STEP = new Easing("smoothStep") {
        public double ease(double delta) {
            return 3*delta*delta - 2*delta*delta*delta;
        }
    };

    public static abstract class Back extends Easing {

        public static final double DEFAULT_OVERSHOOT = 1.70158f;

        private double overshoot;

        public Back(String name) {
            this(name, DEFAULT_OVERSHOOT);
        }

        public Back(String name, double overshoot) {
            super(name);
            this.overshoot = overshoot;
        }

        public void setOvershoot(double overshoot) {
            this.overshoot = overshoot;
        }

        public double getOvershoot() {
            return overshoot;
        }
    }

    public static final Easing BACK_IN = new Easing("backIn") {

        @Override
        public double ease(double delta) {
            double s = 1.70158f;
            double c = s + 1;
            return c * pow(delta, 3) - s * pow(delta, 2);
        }
    };

    public static final Easing BACK_OUT = new Easing("backOut") {
        @Override
        public double ease(double delta) {
            double s = 1.70158f;
            double c = s + 1;
            return 1 + c * pow(delta - 1, 3) + s * pow(delta - 1, 2);
        }
    };

    public static final Easing BACK_IN_OUT = new Easing("backInOut") {

        @Override
        public double ease(double delta) {
            double s = 1.70158f;
            double c = s * 1.525f;
            return delta < 0.5f
                    ? (pow(2 * delta, 2) * ((c + 1) * 2 * delta - c)) / 2
                    : (pow(2 * delta - 2, 2) * ((c + 1) * (delta * 2 - 2) + c) + 2) / 2;
        }
    };

    public static final Easing ELASTIC_IN = new Easing("elasticIn"){

        @Override
        public double ease(double delta) {
            if (delta <= 0) {
                return 0;
            }
            if (delta >= 1) {
                return 1;
            }
            double c = (2 * Mth.PI) / 3f;
            return -pow(2, 10 * delta - 10) * Math.sin((delta * 10 - 10.75f) * c);
        }
    };

    public static final Easing ELASTIC_OUT = new Easing("elasticOut") {

        @Override
        public double ease(double delta) {
            if (delta <= 0) {
                return 0;
            }
            if (delta >= 1) {
                return 1;
            }
            double c = (2 * Mth.PI) / 3f;
            return -pow(2, -10 * delta) * Math.sin((delta * 10 - 0.75f) * c) + 1;
        }
    };

    public static final Easing ELASTIC_IN_OUT = new Easing("elasticInOut") {

        @Override
        public double ease(double delta) {
            if (delta <= 0) {
                return 0;
            }
            if (delta >= 1) {
                return 1;
            }

            double c = (2 * Mth.PI) / 4.5f;
            return delta < 0.5
                    ? -(pow(2, 20 * delta - 10) * Math.sin((20 * delta - 11.125f) * c)) / 2
                    : (pow(2, -20 * delta + 10) * Math.sin((20 * delta - 11.125f) * c)) / 2 + 1;
        }
    };

    public static final Easing BOUNCE_IN = new Easing("bounceIn") {
        public double ease(double delta) {
            return 1 - BOUNCE_OUT.ease(1 - delta);
        }
    };

    public static final Easing BOUNCE_OUT = new Easing("bounceOut") {
        public double ease(double delta) {
            double n1 = 7.5625f;
            double d1 = 2.75f;

            if (delta < 1 / d1) {
                return n1 * delta * delta;
            } else if (delta < 2 / d1) {
                return n1 * (delta -= 1.5f / d1) * delta + 0.75f;
            } else if (delta < 2.5 / d1) {
                return n1 * (delta -= 2.25f / d1) * delta + 0.9375f;
            } else {
                return n1 * (delta -= 2.625f / d1) * delta + 0.984375f;
            }
        }
    };

    public static final Easing BOUNCE_IN_OUT = new Easing("bounceInOut") {
        public double ease(double delta) {
            return delta < 0.5
                    ? (1 - BOUNCE_OUT.ease(1 - 2 * delta)) / 2
                    : (1 + BOUNCE_OUT.ease(2 * delta - 1)) / 2;
        }
    };
}