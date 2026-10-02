package team.lodestar.lodestone;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import org.apache.logging.log4j.Logger;
import team.lodestar.lodestone.internal.LodestoneCommon;

public class LodestoneLib {
    public static final Logger LOGGER = LodestoneCommon.LOGGER;
    public static final String LODESTONE = LodestoneCommon.LODESTONE;
    public static final RandomSource RANDOM = LodestoneCommon.RANDOM;

    public static ResourceLocation lodestonePath(String path) {
        return LodestoneCommon.lodestonePath(path);
    }
}
