package team.lodestar.lodestone.modules.toolkit.creative_tab;

import net.minecraft.world.item.CreativeModeTab;

import java.util.Objects;
import java.util.function.BiFunction;

public final class CategorizedCreativeTabPlatform {
    private static volatile BiFunction<CategorizedBuilder, CategorizedCreativeTab, CreativeModeTab> factory;

    private CategorizedCreativeTabPlatform() {
    }

    public static void install(BiFunction<CategorizedBuilder, CategorizedCreativeTab, CreativeModeTab> factory) {
        CategorizedCreativeTabPlatform.factory = Objects.requireNonNull(factory);
    }

    public static CreativeModeTab create(CategorizedBuilder builder, CategorizedCreativeTab tab) {
        BiFunction<CategorizedBuilder, CategorizedCreativeTab, CreativeModeTab> currentFactory = factory;
        if (currentFactory == null) {
            throw new IllegalStateException("Categorized creative tab platform has not been installed");
        }
        return currentFactory.apply(builder, tab);
    }
}
