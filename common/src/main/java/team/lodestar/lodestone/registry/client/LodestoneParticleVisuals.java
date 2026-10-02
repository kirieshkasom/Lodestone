package team.lodestar.lodestone.registry.client;

import team.lodestar.lodestone.internal.LodestoneCommon;
import team.lodestar.lodestone.modules.rendering.RenderPhase;
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
            ParticleVisualType.<BillboardVisualConfig>builder(LodestoneCommon.lodestonePath("billboard"))
                    .configFactory(BillboardVisualConfig::new)
                    .runtimeFactory(BillboardVisualRuntime::new)
                    .renderPhase(RenderPhase.AFTER_PARTICLES)
                    .build()
    );

    public static final ParticleVisualType<TrailVisualConfig> TRAIL = LodestoneParticleVisuals.register(
            ParticleVisualType.<TrailVisualConfig>builder(LodestoneCommon.lodestonePath("trail"))
                    .configFactory(TrailVisualConfig::new)
                    .runtimeFactory(TrailVisualRuntime::new)
                    .renderPhase(RenderPhase.AFTER_PARTICLES)
                    .build()
    );

    public static final ParticleVisualType<MeshVisualConfig> MESH = LodestoneParticleVisuals.register(
            ParticleVisualType.<MeshVisualConfig>builder(LodestoneCommon.lodestonePath("mesh"))
                    .configFactory(MeshVisualConfig::new)
                    .runtimeFactory(MeshVisualRuntime::new)
                    .renderPhase(RenderPhase.AFTER_PARTICLES)
                    .build()
    );

    public static final ParticleVisualType<QuadVisualConfig> QUAD = LodestoneParticleVisuals.register(
            ParticleVisualType.<QuadVisualConfig>builder(LodestoneCommon.lodestonePath("quad"))
                    .configFactory(QuadVisualConfig::new)
                    .runtimeFactory(QuadVisualRuntime::new)
                    .renderPhase(RenderPhase.AFTER_PARTICLES)
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
