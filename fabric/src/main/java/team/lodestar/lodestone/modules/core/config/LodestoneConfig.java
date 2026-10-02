package team.lodestar.lodestone.modules.core.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import team.lodestar.lodestone.internal.config.ConfigDefinition;
import team.lodestar.lodestone.internal.config.ConfigDefinitionRegistry;
import team.lodestar.lodestone.internal.config.ConfigValue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;

public final class LodestoneConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final ConcurrentMap<Path, Object> FILE_LOCKS = new ConcurrentHashMap<>();
    private static final int MAX_STRING_LENGTH = 32767;
    private static final int MAX_LIST_SIZE = 4096;

    public LodestoneConfig(String modId, String configType) {
        Path file = getConfigPath(modId, configType);
        synchronized (fileLock(file)) {
            JsonObject root = read(file);
            ArrayList<PendingBinding<?>> bindings = new ArrayList<>();
            for (ConfigDefinition<?> definition : ConfigDefinitionRegistry.get(modId, configType)) {
                bindings.add(prepareDefinition(file, root, definition));
            }
            write(file, root);
            for (PendingBinding<?> binding : bindings) {
                binding.bind();
            }
        }
    }

    private static Object fileLock(Path file) {
        return FILE_LOCKS.computeIfAbsent(file.toAbsolutePath().normalize(), ignored -> new Object());
    }

    private static Path getConfigPath(String modId, String configType) {
        validateSegment(modId);
        validateSegment(configType);
        return FabricLoader.getInstance().getConfigDir().resolve("lodestone").resolve(modId).resolve(configType + ".json");
    }

    private static void validateSegment(String segment) {
        if (segment == null || !segment.matches("[A-Za-z0-9_-]+")) {
            throw new IllegalArgumentException("Invalid config path segment: " + segment);
        }
    }

    private static JsonObject read(Path file) {
        if (Files.notExists(file)) {
            return new JsonObject();
        }
        try {
            JsonElement json = JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8));
            if (!json.isJsonObject()) {
                throw new IllegalStateException("Lodestone config root must be a JSON object: " + file);
            }
            return json.getAsJsonObject();
        } catch (IOException | RuntimeException exception) {
            if (exception instanceof IllegalStateException stateException) {
                throw stateException;
            }
            throw new IllegalStateException("Unable to read Lodestone config at " + file, exception);
        }
    }

    private static void write(Path file, JsonObject root) {
        Path temporaryFile = null;
        try {
            Files.createDirectories(file.getParent());
            temporaryFile = Files.createTempFile(file.getParent(), file.getFileName().toString(), ".tmp");
            Files.writeString(temporaryFile, GSON.toJson(root), StandardCharsets.UTF_8);
            try {
                Files.move(temporaryFile, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporaryFile, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to save Lodestone config at " + file, exception);
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException exception) {
                    throw new IllegalStateException("Unable to remove temporary Lodestone config at " + temporaryFile, exception);
                }
            }
        }
    }

    private static JsonObject getOrCreateObject(JsonObject root, String[] path, int length) {
        JsonObject current = root;
        for (int index = 0; index < length; index++) {
            String segment = path[index];
            validateSegment(segment);
            JsonElement child = current.get(segment);
            if (child == null || !child.isJsonObject()) {
                JsonObject object = new JsonObject();
                current.add(segment, object);
                current = object;
            } else {
                current = child.getAsJsonObject();
            }
        }
        return current;
    }

    private static <T> PendingBinding<T> prepareDefinition(Path file, JsonObject root, ConfigDefinition<T> definition) {
        T defaultValue = validateSupported(definition.getDefaultValue());
        String[] path = definition.getPath();
        JsonObject section = getOrCreateObject(root, path, path.length - 1);
        String key = path[path.length - 1];
        T loadedValue = defaultValue;
        JsonElement element = section.get(key);
        if (element != null) {
            try {
                T decodedValue = validateSupported(decode(element, defaultValue));
                loadedValue = definition.validate(decodedValue);
            } catch (IllegalArgumentException ignored) {
                loadedValue = defaultValue;
            }
        }
        section.add(key, GSON.toJsonTree(loadedValue));
        return new PendingBinding<>(definition, new StoredValue<>(file, definition, loadedValue));
    }

    public static final class ConfigValueHolder<T> extends ConfigDefinition<T> {
        public ConfigValueHolder(String modId, String path, T defaultValue) {
            super(modId, path, defaultValue);
        }

        public ConfigValueHolder(String modId, String path, T defaultValue, Predicate<T> validator) {
            super(modId, path, defaultValue, validator);
        }

        public T getConfigValue() {
            return get();
        }

        public void setConfigValue(T value) {
            set(value);
        }
    }

    private static final class StoredValue<T> implements ConfigValue<T> {
        private final Path file;
        private final ConfigDefinition<T> definition;
        private T value;

        private StoredValue(Path file, ConfigDefinition<T> definition, T value) {
            this.file = file;
            this.definition = definition;
            this.value = value;
        }

        @Override
        public synchronized T get() {
            return copyValue(value);
        }

        @Override
        public synchronized void set(T value) {
            T checkedValue = validateSupported(definition.validate(value));
            synchronized (fileLock(file)) {
                JsonObject root = read(file);
                save(root, definition.getPath(), checkedValue);
                write(file, root);
                this.value = checkedValue;
            }
        }

        private static void save(JsonObject root, String[] path, Object value) {
            JsonObject section = getOrCreateObject(root, path, path.length - 1);
            section.add(path[path.length - 1], GSON.toJsonTree(value));
        }
    }

    private record PendingBinding<T>(ConfigDefinition<T> definition, ConfigValue<T> value) {
        private void bind() {
            definition.bind(value);
        }
    }

    private static <T> T validateSupported(T value) {
        if (value instanceof String string) {
            if (string.length() > MAX_STRING_LENGTH) {
                throw new IllegalArgumentException("Config strings may contain at most " + MAX_STRING_LENGTH + " characters");
            }
            return copyValue(value);
        }
        if (value instanceof Boolean || value instanceof Integer || value instanceof Long) {
            return value;
        }
        if (value instanceof Double doubleValue) {
            if (!Double.isFinite(doubleValue)) {
                throw new IllegalArgumentException("Config numbers must be finite");
            }
            return value;
        }
        if (value instanceof Float floatValue) {
            if (!Float.isFinite(floatValue)) {
                throw new IllegalArgumentException("Config numbers must be finite");
            }
            return value;
        }
        if (value instanceof List<?> list && list.size() <= MAX_LIST_SIZE && list.stream().allMatch(String.class::isInstance)) {
            for (Object element : list) {
                if (((String) element).length() > MAX_STRING_LENGTH) {
                    throw new IllegalArgumentException("Config strings may contain at most " + MAX_STRING_LENGTH + " characters");
                }
            }
            @SuppressWarnings("unchecked")
            T copy = (T) List.copyOf(list);
            return copy;
        }
        throw new IllegalArgumentException("Unsupported config value type: " + (value == null ? "null" : value.getClass().getName()));
    }

    @SuppressWarnings("unchecked")
    private static <T> T decode(JsonElement element, T typeReference) {
        if (typeReference instanceof String) {
            if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString()) {
                throw new IllegalArgumentException("Expected string config value");
            }
            return (T) element.getAsString();
        }
        if (typeReference instanceof Boolean) {
            if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isBoolean()) {
                throw new IllegalArgumentException("Expected boolean config value");
            }
            return (T) Boolean.valueOf(element.getAsBoolean());
        }
        if (typeReference instanceof Integer) {
            return (T) Integer.valueOf(readInteger(element));
        }
        if (typeReference instanceof Long) {
            return (T) Long.valueOf(readLong(element));
        }
        if (typeReference instanceof Double) {
            return (T) Double.valueOf(readDouble(element));
        }
        if (typeReference instanceof Float) {
            return (T) Float.valueOf((float) readDouble(element));
        }
        if (typeReference instanceof List<?>) {
            if (!element.isJsonArray() || element.getAsJsonArray().size() > MAX_LIST_SIZE) {
                throw new IllegalArgumentException("Expected bounded string list config value");
            }
            ArrayList<String> strings = new ArrayList<>();
            for (JsonElement item : element.getAsJsonArray()) {
                if (!item.isJsonPrimitive() || !item.getAsJsonPrimitive().isString()) {
                    throw new IllegalArgumentException("Expected string list config value");
                }
                strings.add(item.getAsString());
            }
            return (T) List.copyOf(strings);
        }
        throw new IllegalArgumentException("Unsupported config value type");
    }

    private static int readInteger(JsonElement element) {
        long value = readLong(element);
        if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Integer config value is out of range");
        }
        return (int) value;
    }

    private static long readLong(JsonElement element) {
        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isNumber()) {
            throw new IllegalArgumentException("Expected numeric config value");
        }
        try {
            return element.getAsBigDecimal().longValueExact();
        } catch (ArithmeticException exception) {
            throw new IllegalArgumentException("Expected integer config value", exception);
        }
    }

    private static double readDouble(JsonElement element) {
        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isNumber()) {
            throw new IllegalArgumentException("Expected numeric config value");
        }
        double value = element.getAsDouble();
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Config numbers must be finite");
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    private static <T> T copyValue(T value) {
        if (value instanceof List<?> list) {
            return (T) List.copyOf(list);
        }
        return value;
    }
}
