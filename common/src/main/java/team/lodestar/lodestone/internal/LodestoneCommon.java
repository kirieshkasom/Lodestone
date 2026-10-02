package team.lodestar.lodestone.internal;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.lodestar.lodestone.internal.registration.LodestoneParticles;
import team.lodestar.lodestone.internal.registration.ParticleRegistrar;
public final class LodestoneCommon {
    public static final Logger LOGGER = LogManager.getLogger("team.lodestar.lodestone.LodestoneLib");
    public static final String LODESTONE = "lodestone";
    public static final RandomSource RANDOM = RandomSource.create();

    private LodestoneCommon() {
    }

    public static void init(ParticleRegistrar particles) {
        LodestoneParticles.register(particles);
    }

    public static ResourceLocation lodestonePath(String path) {
        return ResourceLocation.fromNamespaceAndPath(LODESTONE, path);
    }
}
