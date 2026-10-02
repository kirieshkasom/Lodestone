package team.lodestar.lodestone.modules.datagen.providers.tag;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class LodestoneTagProvider<T> implements DataProvider {
    private final PackOutput.PathProvider pathProvider;
    private final String name;
    private final Function<T, ResourceLocation> elementKey;
    private final Map<ResourceLocation, List<Entry>> tags = new LinkedHashMap<>();

    protected LodestoneTagProvider(PackOutput output, String registryPath, String name, Function<T, ResourceLocation> elementKey) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "tags/" + registryPath);
        this.name = name;
        this.elementKey = elementKey;
    }

    protected TagAppender<T> tag(TagKey<T> key) {
        return new TagAppender<>(tags.computeIfAbsent(key.location(), ignored -> new ArrayList<>()), elementKey);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        List<CompletableFuture<?>> writes = new ArrayList<>();
        for (Map.Entry<ResourceLocation, List<Entry>> tag : tags.entrySet()) {
            JsonObject json = new JsonObject();
            JsonArray values = new JsonArray();
            for (Entry entry : tag.getValue()) {
                values.add((entry.tag ? "#" : "") + entry.location);
            }
            json.add("values", values);
            writes.add(DataProvider.saveStable(output, json, pathProvider.json(tag.getKey())));
        }
        return CompletableFuture.allOf(writes.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return name;
    }

    private record Entry(ResourceLocation location, boolean tag) {
    }

    public static final class TagAppender<T> {
        private final List<Entry> entries;
        private final Function<T, ResourceLocation> elementKey;

        private TagAppender(List<Entry> entries, Function<T, ResourceLocation> elementKey) {
            this.entries = entries;
            this.elementKey = elementKey;
        }

        @SafeVarargs
        public final TagAppender<T> add(T... values) {
            for (T value : values) {
                entries.add(new Entry(elementKey.apply(value), false));
            }
            return this;
        }

        @SafeVarargs
        public final TagAppender<T> addTags(TagKey<T>... tags) {
            for (TagKey<T> tag : tags) {
                entries.add(new Entry(tag.location(), true));
            }
            return this;
        }

    }
}
