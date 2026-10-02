package team.lodestar.lodestone.modules.rendering.handlers;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import team.lodestar.lodestone.modules.rendering.particle.pooled.builder.ParticleSpec;
import team.lodestar.lodestone.modules.rendering.particle.pooled.pool.ParticlePool;
import team.lodestar.lodestone.modules.rendering.particle.pooled.pool.ParticlePoolGroup;
import team.lodestar.lodestone.modules.rendering.particle.pooled.pool.ParticlePoolKey;
import team.lodestar.lodestone.modules.rendering.particle.pooled.runtime.ParticleSpawnContext;
import team.lodestar.lodestone.modules.rendering.particle.pooled.visual.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(value = Dist.CLIENT)
public class ParticleHandler {
    private static final int DEFAULT_POOL_CAPACITY = 1000;
    private static final Map<ParticlePoolKey, ParticlePoolGroup> poolGroups = new ConcurrentHashMap<>();
    private static final Map<Integer, CompiledParticleVisualSet> compiledVisuals = new ConcurrentHashMap<>();
    private static final ParticleVisualCollector collector = new ParticleVisualCollector();

    public static void spawn(ParticleSpec spec, ParticleSpawnContext ctx) {
        ParticlePoolGroup group = getPoolGroup(spec);
        spawn(spec, ctx, group);
    }

    public static void spawn(ParticleSpec spec, ParticleSpawnContext ctx, int count) {
        ParticlePoolGroup group = getPoolGroup(spec);
        for (int i = 0; i < count; i++) {
            spawn(spec, ctx, group);
        }
    }

    public static void spawn(ParticleSpec spec, ParticleSpawnContext ctx, ParticlePoolGroup group) {
        compiledVisuals.computeIfAbsent(spec.visualId(), id -> new CompiledParticleVisualSet(spec.visuals()));
        group.claimPool(spec).spawn(spec, ctx);
    }

    public static ParticlePoolGroup getPoolGroup(ParticleSpec spec) {
        ParticlePoolKey key = ParticlePoolKey.fromSpec(spec);
        return poolGroups.computeIfAbsent(key, k -> new ParticlePoolGroup(k, DEFAULT_POOL_CAPACITY));
    }

    private static void tick() {
        for (ParticlePoolGroup group : poolGroups.values()) {
            for (ParticlePool pool : group.pools()) {
                pool.tick(1.0f);
            }
        }
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Pre event) {
        if (event.getLevel().isClientSide()) {
            tick();
        }
    }

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel().isClientSide()) {
            for (ParticlePoolGroup group : poolGroups.values()) {
                for (ParticlePool pool : group.pools()) {
                    pool.clear();
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;

        collector.clear();

        float partialTicks = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        for (ParticlePoolGroup group : poolGroups.values()) {
            for (ParticlePool pool : group.pools()) {
                if (pool.count() <= 0) continue;

                pool.preRender(partialTicks);
                for (int visualId : pool.getActiveVisualIds()) {
                    CompiledParticleVisualSet visuals = compiledVisuals.get(visualId);
                    if (visuals == null || visuals.isEmpty()) continue;

                    ParticleVisualCollectContext ctx = new ParticleVisualCollectContext(collector, pool, visualId);
                    visuals.collect(pool, ctx);
                }
            }
        }

        if (collector.isEmpty()) return;

        Map<ParticleVisualBatchKey, List<ParticleVisualSubmission>> batches = new LinkedHashMap<>();
        for (ParticleVisualSubmission submission : collector.submissions()) {
            ParticleVisualBatchKey key = ParticleVisualBatchKey.fromSubmission(submission);
            batches.computeIfAbsent(key, k -> new ArrayList<>()).add(submission);
        }

        for (Map.Entry<ParticleVisualBatchKey, List<ParticleVisualSubmission>> entry : batches.entrySet()) {
            List<ParticleVisualSubmission> submissions = entry.getValue();
            ParticleVisualBatchRenderer renderer = submissions.getFirst().renderer();
            renderer.renderBatch(entry.getKey(), submissions, event.getPartialTick(), event.getModelViewMatrix(), event.getProjectionMatrix());
        }
    }

    public static Collection<ParticlePoolGroup> allPoolGroups() {
        return Collections.unmodifiableCollection(poolGroups.values());
    }
}