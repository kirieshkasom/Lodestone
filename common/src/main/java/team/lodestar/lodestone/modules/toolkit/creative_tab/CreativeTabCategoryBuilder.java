package team.lodestar.lodestone.modules.toolkit.creative_tab;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ItemLike;
import team.lodestar.lodestone.modules.toolkit.creative_tab.entries.CategoryItemEntry;
import team.lodestar.lodestone.modules.toolkit.creative_tab.entries.CreativeTabCategoryEntry;
import team.lodestar.lodestone.modules.toolkit.creative_tab.entries.NextLineEntry;

import java.util.*;
import java.util.function.*;

@SuppressWarnings("UnusedReturnValue")
public class CreativeTabCategoryBuilder {

    protected final CategorizedCreativeTab categorizedTab;
    protected final ResourceLocation id;

    protected Function<CreativeTabCategory, CreativeTabHeader> header = CreativeTabHeader::new;
    protected final ArrayList<CreativeTabCategoryEntry> entries = new ArrayList<>();

    public CreativeTabCategoryBuilder(CategorizedCreativeTab categorizedTab, ResourceLocation id) {
        this.categorizedTab = categorizedTab;
        this.id = id;
    }

    public CreativeTabCategoryBuilder setHeader(Function<CreativeTabCategory, CreativeTabHeader> header) {
        this.header = header;
        return this;
    }

    public final CreativeTabCategoryBuilder addItems(Consumer<CreativeTabCategoryBuilder> itemAdder) {
        itemAdder.accept(this);
        return this;
    }

    public CreativeTabCategoryBuilder addItems(ItemLike... items) {
        for (ItemLike item : items) {
            addItem(item);
        }
        return this;
    }

    @SafeVarargs
    public final CreativeTabCategoryBuilder addItemStacks(Supplier<ItemStack>... items) {
        for (Supplier<ItemStack> stack : items) {
            addItemStack(stack);
        }
        return this;
    }

    public final CreativeTabCategoryBuilder addItem(ItemLike item) {
        return addItemStack(item.asItem()::getDefaultInstance);
    }

    public final CreativeTabCategoryBuilder addItemStack(ItemStack item) {
        return addItemStack(() -> item);
    }

    public final CreativeTabCategoryBuilder addItemStack(Supplier<ItemStack> item) {
        return addEntry(new CategoryItemEntry(item));
    }

    public CreativeTabCategoryBuilder nextLine() {
        return addEntry(NextLineEntry.INSTANCE);
    }

    public CreativeTabCategoryBuilder addEntries(CreativeTabCategoryEntry... entries) {
        for (CreativeTabCategoryEntry entry : entries) {
            addEntry(entry);
        }
        return this;
    }

    @SafeVarargs
    public final <T> CreativeTabCategoryBuilder addEntries(Function<T, CreativeTabCategoryEntry> function, T... entries) {
        for (T entry : entries) {
            addEntry(function.apply(entry));
        }
        return this;
    }

    public CreativeTabCategoryBuilder addEntry(CreativeTabCategoryEntry entry) {
        entries.add(entry);
        return this;
    }

    public void bake() {
        categorizedTab.appendCategory(id, new CreativeTabCategory(id, header, entries));
    }
}