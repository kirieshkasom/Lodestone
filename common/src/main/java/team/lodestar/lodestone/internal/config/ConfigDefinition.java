package team.lodestar.lodestone.internal.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class ConfigDefinition<T> implements ConfigValue<T> {
    private final String modId;
    private final String configType;
    private final String[] path;
    private final T defaultValue;
    private final Predicate<T> validator;
    private volatile ConfigValue<T> boundValue;
    private T currentValue;

    public ConfigDefinition(String modId, String path, T defaultValue) {
        this(modId, path, defaultValue, value -> true);
    }

    public ConfigDefinition(String modId, String path, T defaultValue, Predicate<T> validator) {
        this.modId = requireSegment(modId);
        this.validator = Objects.requireNonNull(validator);
        T copiedDefault = copyValue(Objects.requireNonNull(defaultValue));
        validateType(copiedDefault, copiedDefault);
        if (!validator.test(copiedDefault)) {
            throw new IllegalArgumentException("Default config value fails validation");
        }
        this.defaultValue = copiedDefault;
        this.currentValue = copiedDefault;
        ArrayList<String> segments = new ArrayList<>(List.of(Objects.requireNonNull(path).split("/")));
        if (segments.size() < 2) {
            throw new IllegalArgumentException("Config path must include a type and value name");
        }
        this.configType = requireSegment(segments.remove(0));
        this.path = segments.stream().map(ConfigDefinition::requireSegment).toArray(String[]::new);
        ConfigDefinitionRegistry.register(this);
    }

    public String getModId() {
        return modId;
    }

    public String getConfigType() {
        return configType;
    }

    public String[] getPath() {
        return path.clone();
    }

    public T getDefaultValue() {
        return copyValue(defaultValue);
    }

    public Predicate<T> getValidator() {
        return validator;
    }

    public synchronized void bind(ConfigValue<T> value) {
        boundValue = Objects.requireNonNull(value);
    }

    @Override
    public T get() {
        ConfigValue<T> value = boundValue;
        if (value == null) {
            synchronized (this) {
                return copyValue(currentValue);
            }
        }
        return copyValue(value.get());
    }

    @Override
    public void set(T value) {
        T checkedValue = validate(value);
        ConfigValue<T> bound = boundValue;
        if (bound != null) {
            bound.set(copyValue(checkedValue));
        }
        synchronized (this) {
            currentValue = copyValue(checkedValue);
        }
    }

    public T validate(T value) {
        validateType(defaultValue, value);
        T copiedValue = copyValue(value);
        if (!validator.test(copiedValue)) {
            throw new IllegalArgumentException("Config value fails validation");
        }
        return copiedValue;
    }

    private static void validateType(Object defaultValue, Object value) {
        if (value == null) {
            throw new IllegalArgumentException("Config value cannot be null");
        }
        if (defaultValue instanceof List<?> defaults) {
            if (!(value instanceof List<?> values)) {
                throw new IllegalArgumentException("Config value must be a list");
            }
            if (!defaults.stream().allMatch(String.class::isInstance) || !values.stream().allMatch(String.class::isInstance)) {
                throw new IllegalArgumentException("Portable config lists must contain only strings");
            }
            return;
        }
        if (defaultValue.getClass() != value.getClass()) {
            throw new IllegalArgumentException("Config value type must match its default value type");
        }
        if (value instanceof Double number && !Double.isFinite(number)) {
            throw new IllegalArgumentException("Config numbers must be finite");
        }
        if (value instanceof Float number && !Float.isFinite(number)) {
            throw new IllegalArgumentException("Config numbers must be finite");
        }
        if (!(defaultValue instanceof String || defaultValue instanceof Boolean || defaultValue instanceof Integer ||
            defaultValue instanceof Long || defaultValue instanceof Double || defaultValue instanceof Float)) {
            throw new IllegalArgumentException("Unsupported portable config value type: " + defaultValue.getClass().getName());
        }
    }

    private static String requireSegment(String segment) {
        if (segment == null || !segment.matches("[A-Za-z0-9_-]+")) {
            throw new IllegalArgumentException("Invalid config path segment: " + segment);
        }
        return segment;
    }

    @SuppressWarnings("unchecked")
    private static <T> T copyValue(T value) {
        if (value instanceof List<?> list) {
            return (T) List.copyOf(list);
        }
        return value;
    }
}
