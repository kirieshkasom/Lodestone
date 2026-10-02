package team.lodestar.lodestone.modules.rendering.texture;

import com.mojang.blaze3d.pipeline.RenderTarget;

import java.util.Objects;
import java.util.function.Consumer;

public final class StencilPlatform {
    private static volatile Consumer<RenderTarget> nativeEnabler;

    private StencilPlatform() {
    }

    public static void installNativeEnabler(Consumer<RenderTarget> enabler) {
        nativeEnabler = Objects.requireNonNull(enabler);
    }

    static boolean enableNative(RenderTarget target) {
        Consumer<RenderTarget> enabler = nativeEnabler;
        if (enabler == null) {
            return false;
        }
        enabler.accept(target);
        return true;
    }

    public static boolean hasNativeEnabler() {
        return nativeEnabler != null;
    }
}
