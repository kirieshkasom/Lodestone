package team.lodestar.lodestone.fabric;

import net.minecraft.world.item.CreativeModeTab;
import team.lodestar.lodestone.modules.toolkit.creative_tab.CategorizedBuilder;
import team.lodestar.lodestone.modules.toolkit.creative_tab.CategorizedCreativeTab;
import team.lodestar.lodestone.modules.toolkit.creative_tab.CategorizedTabAccess;

public final class FabricCategorizedCreativeTab extends CreativeModeTab implements CategorizedTabAccess {
    private final CategorizedCreativeTab categorizedTab;

    public FabricCategorizedCreativeTab(CategorizedBuilder builder, CategorizedCreativeTab categorizedTab) {
        super(builder.row(), builder.column(), CreativeModeTab.Type.CATEGORY, builder.titleValue(), builder.iconValue(), builder.displayItemsValue());
        this.categorizedTab = categorizedTab;
        this.alignedRight = builder.isAlignedRight();
        this.showTitle = builder.showsTitle();
        this.canScroll = builder.canScroll();
        if (builder.backgroundTextureValue() != null) {
            this.backgroundTexture = builder.backgroundTextureValue();
        }
    }

    @Override
    public CategorizedCreativeTab categorizedTab() {
        return this.categorizedTab;
    }
}
