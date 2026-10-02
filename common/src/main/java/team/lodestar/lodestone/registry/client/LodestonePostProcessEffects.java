package team.lodestar.lodestone.registry.client;

import net.minecraft.client.Minecraft;
import team.lodestar.lodestone.internal.LodestoneCommon;
import team.lodestar.lodestone.modules.rendering.postprocess.PostProcessHandler;
import team.lodestar.lodestone.modules.rendering.postprocess.PostProcessor;
import team.lodestar.lodestone.modules.rendering.postprocess.effects.*;

import java.util.function.Supplier;

public class LodestonePostProcessEffects {
    public static final BloomPostProcessor BLOOM = register(BloomPostProcessor::new);
    public static final PhysicallyBasedBloomPostProcessor PB_BLOOM = register(PhysicallyBasedBloomPostProcessor::new);

    private static <T extends PostProcessor> T register(Supplier<T> supplier) {
        return Minecraft.getInstance() == null ? null : supplier.get();
    }

    public static void setupPostProcessEffects() {
        PostProcessHandler.addInstance(BLOOM);
        PostProcessHandler.addInstance(PB_BLOOM);
    }

    /**
     * Enables bloom for your mod.
     */
    public static void enableBloom() {
        BLOOM.setActive(true);
    }

    /**
     * Permanently disables bloom and prevents other mods from enabling it.
     * Use this if your mod is incompatible with bloom.
     */
    public static void forceDisableBloom() {
        BLOOM.forceDisable();
    }

    /**
     * Enables physically based bloom for your mod.
     */
    public static void enablePhysicallyBasedBloom() {
        PB_BLOOM.setActive(true);
    }

    /**
     * Permanently disables physically based bloom and prevents other mods from enabling it.
     * Use this if your mod is incompatible with physically based bloom.
     */
    public static void forceDisablePhysicallyBasedBloom() {
        PB_BLOOM.forceDisable();
    }
}
