package team.lodestar.lodestone.neoforge;

import team.lodestar.lodestone.modules.toolkit.creative_tab.CategorizedCreativeTabPlatform;

public final class NeoForgeCategorizedCreativeTabFactory {
    private NeoForgeCategorizedCreativeTabFactory() {
    }

    public static void install() {
        CategorizedCreativeTabPlatform.install(NeoForgeCategorizedCreativeTab::new);
    }
}
