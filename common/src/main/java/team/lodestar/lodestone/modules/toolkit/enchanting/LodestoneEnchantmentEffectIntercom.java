package team.lodestar.lodestone.modules.toolkit.enchanting;

import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * A class designed to ferry custom info cached for use in more complex enchantment effects.
 */
public class LodestoneEnchantmentEffectIntercom<T extends LodestoneEnchantmentEffectIntercom.EnchantmentIntercomData> {

    public record AttachedIntercom<T extends LodestoneEnchantmentEffectIntercom.EnchantmentIntercomData>(LodestoneEnchantmentEffectIntercom<T> intercom, Supplier<T> dataBuilder) {

        public void write() {
            intercom.write(dataBuilder.get());
        }

        public void clear() {
            intercom.clear();
        }
    }

    protected final AtomicReference<T> cache = new AtomicReference<>();
    protected final Class<T> dataType;

    public LodestoneEnchantmentEffectIntercom(Class<T> dataType) {
        this.dataType = dataType;
    }

    public static <K extends EnchantmentIntercomData> LodestoneEnchantmentEffectIntercom<K> createFor(Class<K> dataType) {
        return new LodestoneEnchantmentEffectIntercom<>(dataType);
    }

    public AttachedIntercom<T> attach(Supplier<T> data) {
        return new AttachedIntercom<>(this, data);
    }

    public void write(T data) {
        cache.set(data);
    }

    public void clear() {
        cache.set(null);
    }

    public Optional<T> read(Level level) {
        var value = cache.get();
        if (value == null) {
            return Optional.empty();
        }
        if (!dataType.isInstance(value)) {
            return Optional.empty();
        }
        if (value.isOutdated(level)) {
            return Optional.empty();
        }
        return Optional.of(value);
    }

    public abstract static class EnchantmentIntercomData {
        protected final long timestamp;

        protected EnchantmentIntercomData(Level level) {
            this.timestamp = level.getGameTime();
        }

        public boolean isOutdated(Level level) {
            return level.getGameTime() != timestamp;
        }
    }
}