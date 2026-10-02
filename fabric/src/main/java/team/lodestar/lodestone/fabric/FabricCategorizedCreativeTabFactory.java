package team.lodestar.lodestone.fabric;

import team.lodestar.lodestone.modules.toolkit.creative_tab.CategorizedCreativeTabPlatform;

public final class FabricCategorizedCreativeTabFactory {
    private FabricCategorizedCreativeTabFactory() {
    }

    public static void install() {
        CategorizedCreativeTabPlatform.install(FabricCategorizedCreativeTab::new);
    }
}
