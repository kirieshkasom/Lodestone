package team.lodestar.lodestone.modules.toolkit.creative_tab;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("NullableProblems")
public class CategorizedBuilder extends CreativeModeTab.Builder {

    public CreativeTabVisualInfo visualInfo;

    public CategorizedBuilder(Function<CategorizedBuilder, CategorizedCreativeTab> tabFactory, CreativeModeTab.Row row, int column) {
        super(row, column);
        withTabFactory(b -> tabFactory.apply((CategorizedBuilder) b));
    }

    @SafeVarargs
    public final CategorizedBuilder withTabsBefore(Holder<? extends CreativeModeTab>... tabs) {
        for (Holder<? extends CreativeModeTab> tab : tabs) {
            var key = tab.getKey();
            assert key != null;
            withTabsBefore(key.location());
        }
        return this;
    }

    public CategorizedBuilder withVisualInfo(CreativeTabVisualInfo visualInfo) {
        this.visualInfo = visualInfo;
        return this;
    }

    @Override
    public CategorizedBuilder title(Component title) {
        return (CategorizedBuilder) super.title(title);
    }

    @Override
    public CategorizedBuilder icon(Supplier<ItemStack> icon) {
        return (CategorizedBuilder) super.icon(icon);
    }

    @Override
    public CategorizedBuilder displayItems(CreativeModeTab.DisplayItemsGenerator displayItemsGenerator) {
        return (CategorizedBuilder) super.displayItems(displayItemsGenerator);
    }

    @Override
    public CategorizedBuilder alignedRight() {
        return (CategorizedBuilder) super.alignedRight();
    }

    @Override
    public CategorizedBuilder hideTitle() {
        return (CategorizedBuilder) super.hideTitle();
    }

    @Override
    public CategorizedBuilder noScrollBar() {
        return (CategorizedBuilder) super.noScrollBar();
    }

    @Override
    protected CategorizedBuilder type(CreativeModeTab.Type type) {
        return (CategorizedBuilder) super.type(type);
    }

    @Override
    public CategorizedBuilder backgroundTexture(ResourceLocation backgroundTexture) {
        return (CategorizedBuilder) super.backgroundTexture(backgroundTexture);
    }

    @Override
    public CategorizedBuilder withSearchBar() {
        return (CategorizedBuilder) super.withSearchBar();
    }

    @Override
    public CategorizedBuilder withSearchBar(int searchBarWidth) {
        return (CategorizedBuilder) super.withSearchBar(searchBarWidth);
    }

    @Override
    public CategorizedBuilder withScrollBarSpriteLocation(ResourceLocation scrollBarSpriteLocation) {
        return (CategorizedBuilder) super.withScrollBarSpriteLocation(scrollBarSpriteLocation);
    }

    @Override
    public CategorizedBuilder withTabsImage(ResourceLocation tabsImage) {
        return (CategorizedBuilder) super.withTabsImage(tabsImage);
    }

    @Override
    public CategorizedBuilder withLabelColor(int labelColor) {
        return (CategorizedBuilder) super.withLabelColor(labelColor);
    }

    @Override
    public CategorizedBuilder withSlotColor(int slotColor) {
        return (CategorizedBuilder) super.withSlotColor(slotColor);
    }

    @Override
    public CategorizedBuilder withTabFactory(Function<CreativeModeTab.Builder, CreativeModeTab> tabFactory) {
        return (CategorizedBuilder) super.withTabFactory(tabFactory);
    }

    @Override
    public CategorizedBuilder withTabsBefore(ResourceLocation... tabs) {
        return (CategorizedBuilder) super.withTabsBefore(tabs);
    }

    @Override
    public CategorizedBuilder withTabsAfter(ResourceLocation... tabs) {
        return (CategorizedBuilder) super.withTabsAfter(tabs);
    }

    @Override
    public CategorizedBuilder displayItems(Collection<? extends Holder<? extends ItemLike>> collection) {
        return (CategorizedBuilder) super.displayItems(collection);
    }
}
