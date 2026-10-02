package team.lodestar.lodestone.internal.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class ConfigDefinitionRegistry {
    private static final ConcurrentMap<Key, List<ConfigDefinition<?>>> DEFINITIONS = new ConcurrentHashMap<>();

    private ConfigDefinitionRegistry() {
    }

    static void register(ConfigDefinition<?> definition) {
        Key key = new Key(definition.getModId(), definition.getConfigType());
        DEFINITIONS.compute(key, (ignored, current) -> {
            List<ConfigDefinition<?>> definitions = current == null ? Collections.synchronizedList(new ArrayList<>()) : current;
            definitions.add(definition);
            return definitions;
        });
    }

    public static List<ConfigDefinition<?>> get(String modId, String configType) {
        List<ConfigDefinition<?>> definitions = DEFINITIONS.get(new Key(modId, configType));
        if (definitions == null) {
            return List.of();
        }
        synchronized (definitions) {
            return List.copyOf(definitions);
        }
    }

    private record Key(String modId, String configType) {
    }
}
