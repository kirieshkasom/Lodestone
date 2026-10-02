package team.lodestar.lodestone.modules.datagen.modular;

import com.google.common.collect.*;
import com.google.gson.*;
import com.mojang.datafixers.util.*;
import com.mojang.serialization.*;
import net.minecraft.core.*;
import net.minecraft.data.*;
import net.minecraft.resources.*;
import team.lodestar.lodestone.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

import static net.minecraft.data.PackOutput.Target.*;

public class ModularDatagenProvider implements DataProvider {

    public final LinkedHashMultimap<ResourceLocation, ModularDatagenBody> data = LinkedHashMultimap.create();

    protected final PackOutput packOutput;
    protected final CompletableFuture<HolderLookup.Provider> lookupProvider;
    protected final String modId;

    public ModularDatagenProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId) {
        this.packOutput = packOutput;
        this.lookupProvider = lookupProvider;
        this.modId = modId;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        HashMap<String, PackOutput.PathProvider> paths = new HashMap<>();
        ImmutableList.Builder<CompletableFuture<?>> futuresBuilder = new ImmutableList.Builder<>();

        return lookupProvider.thenCompose(provider -> {
            var dynamicOps = RegistryOps.create(JsonOps.INSTANCE, provider);
            forAllMatchingData(ModularDatagenJsonBody.class, builder -> {

                futuresBuilder.add(CompletableFuture.supplyAsync(() -> {
                    var path = builder.getDataLocation();
                    var key = builder.getId();
                    path = path + "/" + key.getNamespace();
                    if (!paths.containsKey(path)) {
                        paths.put(path, packOutput.createPathProvider(DATA_PACK, path));
                    }
                    key = ResourceLocation.fromNamespaceAndPath(modId, key.getPath());

                    var instance = builder.build(this, dynamicOps, provider);
                    JsonElement json = null;
                    try {
                        json  = builder.buildJson(dynamicOps, instance);
                    } catch (Exception e) {
                        LodestoneLib.LOGGER.warn("Failed to parse data from json body: {}", builder);
                    }
                    return Pair.of(json, paths.get(path).json(key));
                }).thenComposeAsync((encoded) -> DataProvider.saveStable(cache, encoded.getFirst(), encoded.getSecond())));

            });
            return CompletableFuture.allOf(futuresBuilder.build().toArray(CompletableFuture[]::new));
        });
    }

    public <T extends ModularDatagenBody> void forAllMatchingData(Class<T> type, Consumer<T> consumer) {
        for (ModularDatagenBody value : data.values()) {
            if (type.isInstance(value)) {
                consumer.accept(type.cast(value));
            }
        }
    }

    public void forAllData(Consumer<ModularDatagenBody> consumer) {
        for (ModularDatagenBody value : data.values()) {
            consumer.accept(value);
        }
    }

    public <T extends ModularDatagenBody> T addData(ResourceLocation key, Function<ResourceLocation, T> entry) {
        return addFancyData(key, entry.apply(key));
    }

    public <T extends ModularDatagenBody> T addFancyData(ResourceLocation key, T entry) {
        data.put(key, entry);
        return entry;
    }

    @Override
    public String getName() {
        return "Modular Datagen";
    }
}
