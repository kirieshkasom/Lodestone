package team.lodestar.lodestone.neoforge;

import net.minecraft.world.item.CreativeModeTab;
import team.lodestar.lodestone.modules.toolkit.creative_tab.CategorizedBuilder;
import team.lodestar.lodestone.modules.toolkit.creative_tab.CategorizedCreativeTab;
import team.lodestar.lodestone.modules.toolkit.creative_tab.CategorizedTabAccess;

public final class NeoForgeCategorizedCreativeTab extends CreativeModeTab implements CategorizedTabAccess {
    private final CategorizedCreativeTab categorizedTab;

    public NeoForgeCategorizedCreativeTab(CategorizedBuilder builder, CategorizedCreativeTab categorizedTab) {
        super(toVanillaBuilder(builder));
        this.categorizedTab = categorizedTab;
    }

    private static CreativeModeTab.Builder toVanillaBuilder(CategorizedBuilder builder) {
        CreativeModeTab.Builder tabBuilder = CreativeModeTab.builder(builder.row(), builder.column())
                .title(builder.titleValue())
                .icon(builder.iconValue())
                .displayItems(builder.displayItemsValue());
        if (builder.isAlignedRight()) {
            tabBuilder.alignedRight();
        }
        if (!builder.showsTitle()) {
            tabBuilder.hideTitle();
        }
        if (!builder.canScroll()) {
            tabBuilder.noScrollBar();
        }
        if (builder.backgroundTextureValue() != null) {
            tabBuilder.backgroundTexture(builder.backgroundTextureValue());
        }
        if (builder.hasSearchBar()) {
            tabBuilder.withSearchBar(builder.searchBarWidth());
        }
        if (builder.scrollerSprite() != null) {
            tabBuilder.withScrollBarSpriteLocation(builder.scrollerSprite());
        }
        if (builder.tabsImage() != null) {
            tabBuilder.withTabsImage(builder.tabsImage());
        }
        tabBuilder.withLabelColor(builder.labelColor()).withSlotColor(builder.slotColor());
        tabBuilder.withTabsBefore(builder.tabsBefore().toArray(net.minecraft.resources.ResourceLocation[]::new));
        tabBuilder.withTabsAfter(builder.tabsAfter().toArray(net.minecraft.resources.ResourceLocation[]::new));
        return tabBuilder;
    }

    @Override
    public CategorizedCreativeTab categorizedTab() {
        return this.categorizedTab;
    }
}
