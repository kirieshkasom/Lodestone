package team.lodestar.lodestone.internal.registration;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import team.lodestar.lodestone.internal.LodestoneCommon;
import team.lodestar.lodestone.modules.rendering.particle.standard.world.type.LodestoneItemCrumbsParticleType;
import team.lodestar.lodestone.modules.rendering.particle.standard.world.type.LodestoneTerrainParticleType;
import team.lodestar.lodestone.modules.rendering.particle.standard.world.type.LodestoneWorldParticleType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
public final class LodestoneParticles {
    private static final List<Entry<?>> ENTRIES = new ArrayList<>();
    private static boolean registered;

    public static final Supplier<LodestoneWorldParticleType> WISP_PARTICLE = define("wisp", LodestoneWorldParticleType::new);
    public static final Supplier<LodestoneWorldParticleType> SMOKE_PARTICLE = define("smoke", LodestoneWorldParticleType::new);
    public static final Supplier<LodestoneWorldParticleType> SPARKLE_PARTICLE = define("sparkle", LodestoneWorldParticleType::new);
    public static final Supplier<LodestoneWorldParticleType> TWINKLE_PARTICLE = define("twinkle", LodestoneWorldParticleType::new);
    public static final Supplier<LodestoneWorldParticleType> STAR_PARTICLE = define("star", LodestoneWorldParticleType::new);
    public static final Supplier<LodestoneWorldParticleType> SPARK_PARTICLE = define("spark", LodestoneWorldParticleType::new);
    public static final Supplier<LodestoneWorldParticleType> EXTRUDING_SPARK_PARTICLE = define("extruding_spark", LodestoneWorldParticleType::new);
    public static final Supplier<LodestoneWorldParticleType> THIN_EXTRUDING_SPARK_PARTICLE = define("thin_extruding_spark", LodestoneWorldParticleType::new);
    public static final Supplier<LodestoneTerrainParticleType> TERRAIN_PARTICLE = define("terrain", LodestoneTerrainParticleType::new);
    public static final Supplier<LodestoneItemCrumbsParticleType> ITEM_PARTICLE = define("item", LodestoneItemCrumbsParticleType::new);

    private LodestoneParticles() {
    }

    private static <T extends ParticleType<?>> Supplier<T> define(String path, Supplier<T> factory) {
        Entry<T> entry = new Entry<>(LodestoneCommon.lodestonePath(path), factory);
        ENTRIES.add(entry);
        return entry;
    }

    public static void register(ParticleRegistrar registrar) {
        Objects.requireNonNull(registrar, "registrar");
        if (registered) {
            throw new IllegalStateException("Lodestone particles have already been registered");
        }
        registered = true;
        for (Entry<?> entry : ENTRIES) {
            entry.register(registrar);
        }
    }

    private static final class Entry<T extends ParticleType<?>> implements Supplier<T> {
        private final ResourceLocation id;
        private final Supplier<T> factory;
        private Supplier<T> registeredValue;

        private Entry(ResourceLocation id, Supplier<T> factory) {
            this.id = id;
            this.factory = factory;
        }

        private void register(ParticleRegistrar registrar) {
            registeredValue = Objects.requireNonNull(registrar.register(id, factory), "registeredValue");
        }

        @Override
        public T get() {
            if (registeredValue == null) {
                throw new IllegalStateException("Particle accessed before registration: " + id);
            }
            return registeredValue.get();
        }
    }
}
