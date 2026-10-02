package team.lodestar.lodestone.registry.client;

import net.minecraft.resources.ResourceLocation;
import team.lodestar.lodestone.LodestoneLib;
import team.lodestar.lodestone.modules.rendering.particle.pooled.component.types.attractor.AttractorConfig;
import team.lodestar.lodestone.modules.rendering.particle.pooled.component.types.attractor.AttractorStorage;
import team.lodestar.lodestone.modules.rendering.particle.pooled.component.types.boids.BoidsConfig;
import team.lodestar.lodestone.modules.rendering.particle.pooled.component.types.boids.BoidsStorage;
import team.lodestar.lodestone.modules.rendering.particle.pooled.component.types.scale.ScaleConfig;
import team.lodestar.lodestone.modules.rendering.particle.pooled.component.types.scale.ScaleStorage;
import team.lodestar.lodestone.modules.rendering.particle.pooled.component.types.spin.*;
import team.lodestar.lodestone.modules.rendering.particle.pooled.runtime.ParticlePhase;
import team.lodestar.lodestone.modules.rendering.particle.pooled.component.ParticleComponentType;
import team.lodestar.lodestone.modules.rendering.particle.pooled.component.types.color.ColorConfig;
import team.lodestar.lodestone.modules.rendering.particle.pooled.component.types.color.ColorStorage;

import java.util.*;

public class LodestoneParticleComponents {
    private static final Map<ResourceLocation, ParticleComponentType<?>> REGISTRY = new LinkedHashMap<>();
    private static final Map<ParticleComponentType<?>, Integer> REGISTRY_IDS = new HashMap<>();
    private static int nextId = 0;

    public static final ParticleComponentType<ColorConfig> COLOR = LodestoneParticleComponents.register(
            ParticleComponentType.<ColorConfig>builder(LodestoneLib.lodestonePath("color"))
                    .configFactory(ColorConfig::new)
                    .storageFactory(ColorStorage::new)
                    .phases(ParticlePhase.PRE_RENDER)
                    .priority(0)
                    .build()
    );

    public static final ParticleComponentType<ScaleConfig> SCALE = LodestoneParticleComponents.register(
            ParticleComponentType.<ScaleConfig>builder(LodestoneLib.lodestonePath("scale"))
                    .configFactory(ScaleConfig::new)
                    .storageFactory(ScaleStorage::new)
                    .phases(ParticlePhase.PRE_RENDER)
                    .priority(1)
                    .build()
    );

    public static final ParticleComponentType<SpinConfig> SPIN = LodestoneParticleComponents.register(
            ParticleComponentType.<SpinConfig>builder(LodestoneLib.lodestonePath("spin"))
                    .configFactory(SpinConfig::new)
                    .storageFactory(SpinStorage::new)
                    .phases(ParticlePhase.PRE_RENDER)
                    .priority(2)
                    .build()
    );

    public static final ParticleComponentType<BoidsConfig> BOIDS = LodestoneParticleComponents.register(
            ParticleComponentType.<BoidsConfig>builder(LodestoneLib.lodestonePath("boids"))
                    .configFactory(BoidsConfig::new)
                    .storageFactory(BoidsStorage::new)
                    .phases(ParticlePhase.PRE_UPDATE)
                    .priority(100)
                    .build()
    );

    public static final ParticleComponentType<AttractorConfig> ATTRACTOR = LodestoneParticleComponents.register(
            ParticleComponentType.<AttractorConfig>builder(LodestoneLib.lodestonePath("attractor"))
                    .configFactory(AttractorConfig::new)
                    .storageFactory(AttractorStorage::new)
                    .phases(ParticlePhase.PRE_UPDATE)
                    .priority(101)
                    .build()
    );

    public static <T> ParticleComponentType<T> register(ParticleComponentType<T> type) {
        Objects.requireNonNull(type, "type");
        if (REGISTRY.containsKey(type.id())) {
            throw new IllegalArgumentException("Duplicate particle component id: " + type.id());
        }
        REGISTRY.put(type.id(), type);
        REGISTRY_IDS.put(type, nextId++);
        return type;
    }

    public static ParticleComponentType<?> get(ResourceLocation id) {
        return REGISTRY.get(id);
    }

    public static Collection<ParticleComponentType<?>> all() {
        return Collections.unmodifiableCollection(REGISTRY.values());
    }

    public static int getRegistryId(ParticleComponentType<?> type) {
        return REGISTRY_IDS.get(type);
    }
}
