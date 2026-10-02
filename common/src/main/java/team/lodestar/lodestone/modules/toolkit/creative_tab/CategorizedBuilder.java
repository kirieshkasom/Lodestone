package team.lodestar.lodestone.modules.toolkit.creative_tab;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class CategorizedBuilder {
    public CreativeTabVisualInfo visualInfo;

    private final Function<CategorizedBuilder, CategorizedCreativeTab> tabFactory;
    private final CreativeModeTab.Row row;
    private final int column;
    private Component title = Component.empty();
    private Supplier<ItemStack> icon = () -> ItemStack.EMPTY;
    private CreativeModeTab.DisplayItemsGenerator displayItems = (parameters, output) -> {
    };
    private boolean alignedRight;
    private boolean showTitle = true;
    private boolean canScroll = true;
    private ResourceLocation backgroundTexture;

    public CategorizedBuilder(Function<CategorizedBuilder, CategorizedCreativeTab> tabFactory, CreativeModeTab.Row row, int column) {
        this.tabFactory = Objects.requireNonNull(tabFactory);
        this.row = row;
        this.column = column;
    }

    public CategorizedBuilder withVisualInfo(CreativeTabVisualInfo visualInfo) {
        this.visualInfo = visualInfo;
        return this;
    }

    public CategorizedBuilder title(Component title) {
        this.title = title;
        return this;
    }

    public CategorizedBuilder icon(Supplier<ItemStack> icon) {
        this.icon = icon;
        return this;
    }

    public CategorizedBuilder displayItems(CreativeModeTab.DisplayItemsGenerator displayItemsGenerator) {
        this.displayItems = displayItemsGenerator;
        return this;
    }

    public CategorizedBuilder alignedRight() {
        this.alignedRight = true;
        return this;
    }

    public CategorizedBuilder hideTitle() {
        this.showTitle = false;
        return this;
    }

    public CategorizedBuilder noScrollBar() {
        this.canScroll = false;
        return this;
    }

    public CategorizedBuilder backgroundTexture(ResourceLocation backgroundTexture) {
        this.backgroundTexture = backgroundTexture;
        return this;
    }

    public CreativeModeTab build() {
        CategorizedCreativeTab tab = this.tabFactory.apply(this);
        return CategorizedCreativeTabPlatform.create(this, tab);
    }

    public CreativeModeTab.Row row() {
        return this.row;
    }

    public int column() {
        return this.column;
    }

    public Component titleValue() {
        return this.title;
    }

    public Supplier<ItemStack> iconValue() {
        return this.icon;
    }

    public CreativeModeTab.DisplayItemsGenerator displayItemsValue() {
        return this.displayItems;
    }

    public boolean isAlignedRight() {
        return this.alignedRight;
    }

    public boolean showsTitle() {
        return this.showTitle;
    }

    public boolean canScroll() {
        return this.canScroll;
    }

    public ResourceLocation backgroundTextureValue() {
        return this.backgroundTexture;
    }
}
