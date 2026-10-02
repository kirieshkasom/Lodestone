package team.lodestar.lodestone.modules.rendering.particle.pooled.builder;

import team.lodestar.lodestone.modules.rendering.particle.pooled.pool.ParticlePool;
import team.lodestar.lodestone.modules.rendering.particle.pooled.component.ParticleComponentType;
import team.lodestar.lodestone.modules.rendering.particle.pooled.runtime.ParticleSpawnContext;
import team.lodestar.lodestone.modules.rendering.particle.pooled.runtime.ParticleSpawnContextChain;
import team.lodestar.lodestone.modules.rendering.particle.pooled.runtime.profile.ParticleSpawnProfile;
import team.lodestar.lodestone.modules.rendering.particle.pooled.visual.ParticleVisualDict;
import team.lodestar.lodestone.modules.rendering.particle.pooled.visual.ParticleVisualEntry;

import java.util.*;

public class ParticleSpec {
    private static final Comparator<ParticleComponentType<?>> COMPONENT_PRIORITY = Comparator.comparingInt(ParticleComponentType::priority);

    private final Map<ParticleComponentType<?>, Object> componentConfigs;
    private final ParticleComponentType<?>[] orderedComponentTypes;
    private final List<ParticleVisualEntry<?>> visuals;
    private final int visualId;


    public ParticleSpec(Map<ParticleComponentType<?>, Object> componentConfigs, List<ParticleVisualEntry<?>> visuals) {
        this.componentConfigs = Collections.unmodifiableMap(new LinkedHashMap<>(componentConfigs));
        this.visuals = visuals;
        this.visualId = ParticleVisualDict.getId(visuals);

        List<ParticleComponentType<?>> ordered = new ArrayList<>(this.componentConfigs.keySet());
        ordered.sort(COMPONENT_PRIORITY);
        this.orderedComponentTypes = ordered.toArray(new ParticleComponentType<?>[0]);
    }

    public Map<ParticleComponentType<?>, Object> componentConfigs() {
        return componentConfigs;
    }

    public ParticleComponentType<?>[] orderedComponentTypes() {
        return orderedComponentTypes;
    }

    public List<ParticleVisualEntry<?>> visuals() {
        return visuals;
    }

    public int visualId() {
        return visualId;
    }

    public void spawn(ParticlePool pool, ParticleSpawnContext ctx, int count) {
        for (int i = 0; i < count; i++) {
            this.spawn(pool, ctx);
        }
    }

    public void spawn(ParticlePool pool, ParticleSpawnContext ctx) {
        pool.spawn(this, ctx);
    }

    public void spawn(ParticlePool pool, ParticleSpawnProfile profile, int count) {
        this.spawn(pool, new ParticleSpawnContext(), profile, count);
    }

    public void spawn(ParticlePool pool, ParticleSpawnContext ctx, ParticleSpawnProfile profile, int count) {
        for (int i = 0; i < count; i++) {
            ParticleSpawnContext copy = ctx.copy();
            profile.apply(copy);
            pool.spawn(this, copy);
        }
    }

    public void spawn(ParticlePool pool, ParticleSpawnContextChain ctxChain, int count) {
        this.spawn(pool, new ParticleSpawnContext(), ctxChain, count);
    }

    public void spawn(ParticlePool pool, ParticleSpawnContext ctx, ParticleSpawnContextChain ctxChain, int count) {
        for (int i = 0; i < count; i++) {
            pool.spawn(this, ctxChain.apply(ctx.copy(), i, count));
        }
    }
}