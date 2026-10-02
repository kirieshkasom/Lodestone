package team.lodestar.lodestone.modules.datagen.providers;

import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class LodestoneJsonDataProvider implements DataProvider {
    private final PackOutput.PathProvider pathProvider;
    private final String name;
    protected final Map<ResourceLocation, JsonObject> generatedJson = new LinkedHashMap<>();

    protected LodestoneJsonDataProvider(PackOutput output, PackOutput.Target target, String directory, String name) {
        this.pathProvider = output.createPathProvider(target, directory);
        this.name = name;
    }

    protected void save(ResourceLocation location, JsonObject json) {
        generatedJson.put(location, json);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        CompletableFuture<?>[] writes = generatedJson.entrySet().stream()
                .map(entry -> DataProvider.saveStable(output, entry.getValue(), pathProvider.json(entry.getKey())))
                .toArray(CompletableFuture[]::new);
        return CompletableFuture.allOf(writes);
    }

    @Override
    public String getName() {
        return name;
    }
}
