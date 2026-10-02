package team.lodestar.lodestone.modules.rendering.particle.pooled.visual;

import team.lodestar.lodestone.modules.rendering.particle.pooled.pool.ParticlePool;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class CompiledParticleVisualSet {
    //private static final Comparator<RuntimeEntry> VISUAL_PRIORITY = Comparator.comparingInt(a -> a.type.priority());

    private final List<RuntimeEntry> runtimes;

    @SuppressWarnings({"rawtypes", "unchecked"})
    public CompiledParticleVisualSet(List<ParticleVisualEntry<?>> visuals) {
        Objects.requireNonNull(visuals, "visuals");

        List<RuntimeEntry> compiled = new ArrayList<>(visuals.size());
        for (ParticleVisualEntry<?> entry : visuals) {
            ParticleVisualType type = entry.type();
            Object config = entry.config();
            ParticleVisualRuntime runtime = type.createRuntime(config);
            compiled.add(new RuntimeEntry(type, config, runtime));
        }

        //compiled.sort(VISUAL_PRIORITY);
        this.runtimes = List.copyOf(compiled);
    }

    public boolean isEmpty() {
        return runtimes.isEmpty();
    }

    public void collect(ParticlePool pool, ParticleVisualCollectContext context) {
        int liveCount = pool.count();
        if (liveCount <= 0 || runtimes.isEmpty()) {
            return;
        }

        for (RuntimeEntry entry : runtimes) {
            entry.runtime.collect(context, pool, liveCount);
        }
    }

    private record RuntimeEntry(ParticleVisualType<?> type, Object config, ParticleVisualRuntime runtime) {
    }
}