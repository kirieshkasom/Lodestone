package team.lodestar.lodestone.modules.toolkit.creative_tab;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import team.lodestar.lodestone.modules.toolkit.creative_tab.entries.CreativeTabCategoryEntry;

import java.util.*;
import java.util.function.Function;

public final class CreativeTabCategory {
    private final ResourceLocation id;

    private final @Nullable CreativeTabHeader header;
    private final List<CreativeTabCategoryEntry> entries;

    public CreativeTabCategory(ResourceLocation id, @Nullable Function<CreativeTabCategory, CreativeTabHeader> header, List<CreativeTabCategoryEntry> entries) {
        this.id = id;
        this.entries = entries;
        this.header = header != null ? header.apply(this) : null;
    }

    public String getHeaderLangKey() {
        return id.getNamespace() + ".itemGroup.header." + id.getPath();
    }

    public boolean hasHeader() {
        return header != null;
    }

    public @Nullable CreativeTabHeader getHeader() {
        return header;
    }

    public ResourceLocation getId() {
        return id;
    }

    public List<CreativeTabCategoryEntry> getEntries() {
        return entries;
    }
}