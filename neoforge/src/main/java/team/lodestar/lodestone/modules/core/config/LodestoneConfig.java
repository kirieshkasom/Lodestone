package team.lodestar.lodestone.modules.core.config;

import com.mojang.datafixers.util.Pair;
import net.neoforged.neoforge.common.ModConfigSpec;
import team.lodestar.lodestone.internal.config.ConfigDefinition;
import team.lodestar.lodestone.internal.config.ConfigDefinitionRegistry;
import team.lodestar.lodestone.internal.config.ConfigValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LodestoneConfig {

    @SuppressWarnings("rawtypes")
    public static final ConcurrentHashMap<Pair<String, ConfigPath>, ArrayList<ConfigValueHolder>> VALUE_HOLDERS = new ConcurrentHashMap<>();

    @SuppressWarnings("rawtypes")
    public LodestoneConfig(String modId, String configType, ModConfigSpec.Builder builder) {
        for (ConfigDefinition<?> definition : ConfigDefinitionRegistry.get(modId, configType)) {
            bindDefinition(definition, builder);
        }
        for (Map.Entry<Pair<String, ConfigPath>, ArrayList<ConfigValueHolder>> next : VALUE_HOLDERS.entrySet()) {
            Pair<String, ConfigPath> key = next.getKey();
            if (key.getFirst().equals(modId + "/" + configType)) {
                builder.push(List.of(key.getSecond().strings));
                for (ConfigValueHolder configValueHolder : next.getValue()) {
                    configValueHolder.setConfig(builder);
                }
                builder.pop(key.getSecond().strings.length);
            }
        }
    }

    private static <T> void bindDefinition(ConfigDefinition<T> definition, ModConfigSpec.Builder builder) {
        String[] path = definition.getPath();
        if (path.length > 1) {
            builder.push(List.of(Arrays.copyOf(path, path.length - 1)));
        }
        ModConfigSpec.ConfigValue<T> config = builder.define(path[path.length - 1], definition.getDefaultValue(), value -> isValid(definition, value));
        if (path.length > 1) {
            builder.pop(path.length - 1);
        }
        definition.bind(new ConfigValue<>() {
            @Override
            public T get() {
                return config.get();
            }

            @Override
            public void set(T value) {
                config.set(definition.validate(value));
            }
        });
    }

    @SuppressWarnings("unchecked")
    private static <T> boolean isValid(ConfigDefinition<T> definition, Object value) {
        try {
            definition.validate((T) value);
            return true;
        } catch (ClassCastException | IllegalArgumentException exception) {
            return false;
        }
    }

    public static class ConfigValueHolder<T> implements ConfigValue<T> {
        private final BuilderSupplier<T> valueSupplier;
        private final String modId;
        private final String configType;
        private final ConfigPath configPath;
        private ModConfigSpec.ConfigValue<T> config;

        public ConfigValueHolder(String modId, String path, BuilderSupplier<T> valueSupplier) {
            this.modId = modId;
            this.valueSupplier = valueSupplier;
            ArrayList<String> entirePath = new ArrayList<>(List.of(path.split("/")));
            this.configType = entirePath.remove(0);
            this.configPath = new ConfigPath(entirePath.toArray(new String[]{}));
            VALUE_HOLDERS.computeIfAbsent(Pair.of(modId + "/" + configType, configPath), key -> new ArrayList<>()).add(this);
        }

        public void setConfig(ModConfigSpec.Builder builder) {
            config = valueSupplier.createBuilder(builder);
        }

        public void setConfigValue(T value) {
            config.set(value);
        }

        public ModConfigSpec.ConfigValue<T> getConfig() {
            return config;
        }

        public T getConfigValue() {
            return config.get();
        }

        @Override
        public T get() {
            return getConfigValue();
        }

        @Override
        public void set(T value) {
            setConfigValue(value);
        }
    }

    public interface BuilderSupplier<T> {
        ModConfigSpec.ConfigValue<T> createBuilder(ModConfigSpec.Builder builder);
    }

    public record ConfigPath(String... strings) {
        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || getClass() != other.getClass()) {
                return false;
            }
            ConfigPath otherPath = (ConfigPath) other;
            return Arrays.equals(strings, otherPath.strings);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(strings);
        }
    }
}
