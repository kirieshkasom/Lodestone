package team.lodestar.lodestone.registry.client;

import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import team.lodestar.lodestone.LodestoneLib;
import team.lodestar.lodestone.modules.rendering.particle.pooled.visual.ParticleVisualType;
import team.lodestar.lodestone.modules.rendering.particle.pooled.visual.types.billboard.BillboardVisualConfig;
import team.lodestar.lodestone.modules.rendering.particle.pooled.visual.types.billboard.BillboardVisualRuntime;
import team.lodestar.lodestone.modules.rendering.particle.pooled.visual.types.quad.QuadVisualConfig;
import team.lodestar.lodestone.modules.rendering.particle.pooled.visual.types.mesh.MeshVisualConfig;
import team.lodestar.lodestone.modules.rendering.particle.pooled.visual.types.mesh.MeshVisualRuntime;
import team.lodestar.lodestone.modules.rendering.particle.pooled.visual.types.quad.QuadVisualRuntime;
import team.lodestar.lodestone.modules.rendering.particle.pooled.visual.types.trail.TrailVisualConfig;
import team.lodestar.lodestone.modules.rendering.particle.pooled.visual.types.trail.TrailVisualRuntime;

import java.util.*;

public class LodestoneParticleVisuals {
    private static final List<ParticleVisualType<?>> REGISTERED = new ArrayList<>();
    private static final Map<ParticleVisualType<?>, Integer> IDS = new HashMap<>();

    public static final ParticleVisualType<BillboardVisualConfig> BILLBOARD = LodestoneParticleVisuals.register(
            ParticleVisualType.<BillboardVisualConfig>builder(LodestoneLib.lodestonePath("billboard"))
                    .configFactory(BillboardVisualConfig::new)
                    .runtimeFactory(BillboardVisualRuntime::new)
                    .renderStage(RenderLevelStageEvent.Stage.AFTER_PARTICLES)
                    .build()
    );

    public static final ParticleVisualType<TrailVisualConfig> TRAIL = LodestoneParticleVisuals.register(
            ParticleVisualType.<TrailVisualConfig>builder(LodestoneLib.lodestonePath("trail"))
                    .configFactory(TrailVisualConfig::new)
                    .runtimeFactory(TrailVisualRuntime::new)
                    .renderStage(RenderLevelStageEvent.Stage.AFTER_PARTICLES)
                    .build()
    );

    public static final ParticleVisualType<MeshVisualConfig> MESH = LodestoneParticleVisuals.register(
            ParticleVisualType.<MeshVisualConfig>builder(LodestoneLib.lodestonePath("mesh"))
                    .configFactory(MeshVisualConfig::new)
                    .runtimeFactory(MeshVisualRuntime::new)
                    .renderStage(RenderLevelStageEvent.Stage.AFTER_PARTICLES)
                    .build()
    );

    public static final ParticleVisualType<QuadVisualConfig> QUAD = LodestoneParticleVisuals.register(
            ParticleVisualType.<QuadVisualConfig>builder(LodestoneLib.lodestonePath("quad"))
                    .configFactory(QuadVisualConfig::new)
                    .runtimeFactory(QuadVisualRuntime::new)
                    .renderStage(RenderLevelStageEvent.Stage.AFTER_PARTICLES)
                    .build()
    );

    public static <T> ParticleVisualType<T> register(ParticleVisualType<T> type) {
        int id = REGISTERED.size();
        REGISTERED.add(type);
        IDS.put(type, id);
        return type;
    }

    public static int getRegistryId(ParticleVisualType<?> type) {
        Integer id = IDS.get(type);
        if (id == null) {
            throw new NullPointerException("ParticleVisualType: " + type);
        }
        return id;
    }

    public static List<ParticleVisualType<?>> all() {
        return List.copyOf(REGISTERED);
    }
}