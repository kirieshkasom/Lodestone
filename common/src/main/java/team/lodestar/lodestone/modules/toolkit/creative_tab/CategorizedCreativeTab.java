package team.lodestar.lodestone.modules.toolkit.creative_tab;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import net.minecraft.resources.*;
import net.minecraft.world.item.*;
import team.lodestar.lodestone.modules.toolkit.creative_tab.entries.CreativeTabCategoryEntry;
import team.lodestar.lodestone.modules.toolkit.creative_tab.slot.SlotLocation;
import team.lodestar.lodestone.modules.toolkit.creative_tab.slot.SlotStorage;

import java.util.*;
import java.util.function.*;

public abstract class CategorizedCreativeTab {

    protected final String modId;
    protected final HashMap<ResourceLocation, CreativeTabCategory> categories = new LinkedHashMap<>();

    protected final CreativeTabVisualInfo visualInfo;

    protected final Int2ObjectArrayMap<CreativeTabHeader> headerRows = new Int2ObjectArrayMap<>();
    protected final Int2ObjectLinkedOpenHashMap<SlotStorage> slots = new Int2ObjectLinkedOpenHashMap<>();

    protected CategorizedCreativeTab(String modId, CategorizedBuilder categorizedBuilder) {
        this.modId = modId;
        this.visualInfo = categorizedBuilder.visualInfo;
        buildCategories();
    }

    public CreativeTabCategoryBuilder createCategory(String id) {
        return new CreativeTabCategoryBuilder(this, ResourceLocation.fromNamespaceAndPath(modId, id));
    }

    public HashMap<ResourceLocation, CreativeTabCategory> getCategories() {
        return categories;
    }

    public CreativeTabHeader getHeader(int row) {
        return headerRows.get(row);
    }

    public SlotStorage getSlotStorage(int itemIndex) {
        return slots.get(itemIndex);
    }

    public static void buildCreativeTabs(CreativeModeTab tab, Consumer<ItemStack> consumer) {
        if (!(tab instanceof CategorizedTabAccess access)) {
            return;
        }
        CategorizedCreativeTab categorizedTab = access.categorizedTab();
        var location = new SlotLocation();
        for (CreativeTabCategory category : categorizedTab.categories.values()) {
            for (CreativeTabCategoryEntry entry : category.getEntries()) {
                var slot = entry.bake(categorizedTab, location);
                if (slot == null) {
                    continue;
                }
                for (ItemStack storedItem : slot.getStoredItems()) {
                    if (!categorizedTab.isItemVisible(storedItem)) {
                        continue;
                    }
                    consumer.accept(storedItem);
                }
            }
        }
    }

    public void bakeCategoryData(CategorizedCreativeTab tab) {
        headerRows.clear();
        slots.clear();

        var location = new SlotLocation();
        for (CreativeTabCategory category : tab.categories.values()) {
            if (category.hasHeader()) {
                headerRows.put(location.getRow(), category.getHeader());
                location.nextLine(true);
            }
            for (CreativeTabCategoryEntry entry : category.getEntries()) {
                var slot = entry.bake(tab, location);
                if (slot == null) {
                    continue;
                }
                int index = location.getIndex();
                slots.put(index, slot);
                location.step();
            }
            location.nextLine(false);
        }
    }

    public abstract boolean isItemVisible(ItemStack stack);

    public abstract void buildCategories();

    protected void appendCategory(ResourceLocation id, CreativeTabCategory category) {
        categories.put(id, category);
    }

    public static CategorizedBuilder builder(Function<CategorizedBuilder, CategorizedCreativeTab> tabFactory) {
        return new CategorizedBuilder(tabFactory, CreativeModeTab.Row.TOP, 0);
    }

    public static CategorizedBuilder builder(Function<CategorizedBuilder, CategorizedCreativeTab> tabFactory, CreativeModeTab.Row row, int column) {
        return new CategorizedBuilder(tabFactory, row, column);
    }
}
