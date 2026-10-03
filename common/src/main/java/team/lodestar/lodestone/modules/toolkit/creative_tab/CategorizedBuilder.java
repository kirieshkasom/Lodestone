package team.lodestar.lodestone.modules.toolkit.creative_tab;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import java.util.ArrayList;
import java.util.List;
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
    private boolean searchBar;
    private int searchBarWidth = 89;
    private ResourceLocation scrollerSprite;
    private ResourceLocation tabsImage;
    private int labelColor = 4210752;
    private int slotColor = -2130706433;
    private final List<ResourceLocation> tabsBefore = new ArrayList<>();
    private final List<ResourceLocation> tabsAfter = new ArrayList<>();

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

    public CategorizedBuilder withSearchBar() {
        searchBar = true;
        if (backgroundTexture == null) {
            backgroundTexture = CreativeModeTab.createTextureLocation("item_search");
        }
        return this;
    }

    public CategorizedBuilder withSearchBar(int width) {
        if (width <= 0) {
            throw new IllegalArgumentException("Search bar width must be positive");
        }
        searchBarWidth = width;
        return withSearchBar();
    }

    public CategorizedBuilder withScrollBarSpriteLocation(ResourceLocation sprite) {
        scrollerSprite = Objects.requireNonNull(sprite);
        return this;
    }

    public CategorizedBuilder withTabsImage(ResourceLocation image) {
        tabsImage = Objects.requireNonNull(image);
        return this;
    }

    public CategorizedBuilder withLabelColor(int color) {
        labelColor = color;
        return this;
    }

    public CategorizedBuilder withSlotColor(int color) {
        slotColor = color;
        return this;
    }

    public CategorizedBuilder withTabsBefore(ResourceLocation... tabs) {
        tabsBefore.addAll(List.of(tabs));
        return this;
    }

    public CategorizedBuilder withTabsAfter(ResourceLocation... tabs) {
        tabsAfter.addAll(List.of(tabs));
        return this;
    }

    @SafeVarargs
    public final CategorizedBuilder withTabsBefore(ResourceKey<CreativeModeTab>... tabs) {
        for (ResourceKey<CreativeModeTab> tab : tabs) {
            tabsBefore.add(tab.location());
        }
        return this;
    }

    @SafeVarargs
    public final CategorizedBuilder withTabsAfter(ResourceKey<CreativeModeTab>... tabs) {
        for (ResourceKey<CreativeModeTab> tab : tabs) {
            tabsAfter.add(tab.location());
        }
        return this;
    }

    public boolean hasSearchBar() {
        return searchBar;
    }

    public int searchBarWidth() {
        return searchBarWidth;
    }

    public ResourceLocation scrollerSprite() {
        return scrollerSprite;
    }

    public ResourceLocation tabsImage() {
        return tabsImage;
    }

    public int labelColor() {
        return labelColor;
    }

    public int slotColor() {
        return slotColor;
    }

    public List<ResourceLocation> tabsBefore() {
        return List.copyOf(tabsBefore);
    }

    public List<ResourceLocation> tabsAfter() {
        return List.copyOf(tabsAfter);
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
