package team.lodestar.lodestone.modules.core.sound;

import net.minecraft.resources.*;
import net.minecraft.sounds.*;
import org.jetbrains.annotations.*;

import java.util.function.*;

@SuppressWarnings("unused")
public abstract class RegistryReadyBlockSoundType extends ExtendedSoundType {

    protected final Supplier<SoundEvent> breakSound;
    protected final Supplier<SoundEvent> stepSound;
    protected final Supplier<SoundEvent> placeSound;
    protected final Supplier<SoundEvent> hitSound;
    protected final Supplier<SoundEvent> fallSound;

    public RegistryReadyBlockSoundType(Function<SoundEvent, ? extends Supplier<SoundEvent>> registry, Function<String, ResourceLocation> path, String name) {
        this(registry, path, name, 1f, 1f);
    }

    public RegistryReadyBlockSoundType(Function<SoundEvent, ? extends Supplier<SoundEvent>> registry, Function<String, ResourceLocation> path, String name, float volume, float pitch) {
        super(volume, pitch, null, null, null, null, null);
        breakSound = registry.apply(SoundEvent.createVariableRangeEvent(path.apply(name + "_break")));
        placeSound = registry.apply(SoundEvent.createVariableRangeEvent(path.apply(name + "_place")));
        stepSound = registry.apply(SoundEvent.createVariableRangeEvent(path.apply(name + "_step")));
        hitSound = registry.apply(SoundEvent.createVariableRangeEvent(path.apply(name + "_hit")));
        fallSound = registry.apply(SoundEvent.createVariableRangeEvent(path.apply(name + "_fall")));
    }

    @Override
    public @NotNull SoundEvent getBreakSound() {
        return breakSound.get();
    }

    @Override
    public @NotNull SoundEvent getStepSound() {
        return stepSound.get();
    }

    @Override
    public @NotNull SoundEvent getPlaceSound() {
        return placeSound.get();
    }

    @Override
    public @NotNull SoundEvent getHitSound() {
        return hitSound.get();
    }

    @Override
    public @NotNull SoundEvent getFallSound() {
        return fallSound.get();
    }

    public Supplier<SoundEvent> getBreakSoundHolder() {
        return breakSound;
    }

    public Supplier<SoundEvent> getStepSoundHolder() {
        return stepSound;
    }

    public Supplier<SoundEvent> getPlaceSoundHolder() {
        return placeSound;
    }

    public Supplier<SoundEvent> getHitSoundHolder() {
        return hitSound;
    }

    public Supplier<SoundEvent> getFallSoundHolder() {
        return fallSound;
    }
}