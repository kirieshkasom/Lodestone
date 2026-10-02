package team.lodestar.lodestone.modules.rendering.particle.standard;

import it.unimi.dsi.fastutil.floats.*;
import it.unimi.dsi.fastutil.ints.*;
import team.lodestar.lodestone.modules.rendering.particle.standard.data.GenericParticleData;
import team.lodestar.lodestone.modules.rendering.particle.standard.data.color.ColorParticleData;
import team.lodestar.lodestone.modules.rendering.particle.standard.data.spin.SpinParticleData;

import java.awt.*;
import java.util.function.*;

public abstract class SimpleParticleOptions {

    public static final ColorParticleData DEFAULT_COLOR = ColorParticleData.create(Color.WHITE, Color.WHITE).build();
    public static final SpinParticleData DEFAULT_SPIN = SpinParticleData.create(0).build().lock();
    public static final GenericParticleData DEFAULT_GENERIC = GenericParticleData.create(1, 0).build().lock();

    public enum ParticleSpritePicker {
        FIRST_INDEX, LAST_INDEX, WITH_AGE, WITH_AGE_INVERSE, RANDOM_SPRITE
    }

    public ParticleSpritePicker spritePicker = ParticleSpritePicker.WITH_AGE;

    public ColorParticleData colorData = DEFAULT_COLOR;
    public GenericParticleData transparencyData = DEFAULT_GENERIC;
    public GenericParticleData scaleData = DEFAULT_GENERIC;
    public GenericParticleData lengthData = DEFAULT_GENERIC;
    public SpinParticleData spinData = DEFAULT_SPIN;

    public Supplier<Integer> lifetimeSupplier = () -> 20;
    public Supplier<Integer> lifeDelaySupplier = () -> 0;
    public Supplier<Float> gravitySupplier = () -> 0f;
    public Supplier<Float> frictionSupplier = () -> 1f;

    public Int2IntFunction lifetimeModifier = i -> i;
    public Int2IntFunction lifeDelayModifier = i -> i;
    public Float2FloatFunction gravityModifier = f -> f;
    public Float2FloatFunction frictionModifier = f -> f;


    public int getLifetime() {
        return lifetimeModifier.apply(lifetimeSupplier.get());
    }

    public int getLifeDelay() {
        return lifeDelayModifier.apply(lifeDelaySupplier.get());
    }

    public float getGravity() {
        return gravityModifier.apply(gravitySupplier.get());
    }

    public float getFriction() {
        return frictionModifier.apply(frictionSupplier.get());
    }
}