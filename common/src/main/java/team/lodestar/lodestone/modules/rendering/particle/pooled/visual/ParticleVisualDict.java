package team.lodestar.lodestone.modules.rendering.particle.pooled.visual;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParticleVisualDict {
    private static final Map<List<ParticleVisualEntry<?>>, Integer> keyToId = new HashMap<>();
    private static int nextId = 0;

    public static int getId(List<ParticleVisualEntry<?>> visuals) {
        return keyToId.computeIfAbsent(List.copyOf(visuals), k -> nextId++);
    }
}