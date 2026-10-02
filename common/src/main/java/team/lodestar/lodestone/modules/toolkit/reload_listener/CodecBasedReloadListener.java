package team.lodestar.lodestone.modules.toolkit.reload_listener;

import com.google.gson.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import team.lodestar.lodestone.LodestoneLib;
import team.lodestar.lodestone.modules.toolkit.codec.LodestoneCodecs;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class CodecBasedReloadListener<K, T> extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = (new GsonBuilder()).create();

    protected final Map<K, T> data = new HashMap<>();
    protected final Codec<T> lookupCodec;
    protected final Codec<Optional<T>> lookupOptionalCodec;

    public CodecBasedReloadListener(String directory) {
        super(GSON, directory);
        lookupCodec = getKeyCodec().xmap(this::get, this::getID);

        lookupOptionalCodec = LodestoneCodecs.optionalCodec(getKeyCodec()).xmap(
                d -> d.map(this::get),
                d -> d.map(this::getID));
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objects, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        data.clear();
        for (int i = 0; i < objects.size(); i++) {
            var location = (ResourceLocation) objects.keySet().toArray()[i];
            var raw = objects.get(location);
            if (raw.isJsonArray()) {
                JsonArray asArray = raw.getAsJsonArray();
                for (JsonElement jsonElement : asArray) {
                    var object = jsonElement.getAsJsonObject();
                    readData(object, location);
                }
                continue;
            }
            readData(raw, location);
        }
    }

    public void readData(JsonElement element, ResourceLocation location) {
        if (!element.isJsonObject()) {
            return;
        }
        var object = element.getAsJsonObject();
        try {
            var result = getCodec().parse(RegistryOps.create(JsonOps.INSTANCE, getRegistryLookup()), object).result();
            result.ifPresent(b -> data.put(getID(b), b));
        } catch (JsonParseException exception) {
            LodestoneLib.LOGGER.info("Something ominous has occurred... {}, {}", location, exception);
        }

    }

    public abstract Codec<K> getKeyCodec();

    public abstract Codec<T> getCodec();

    public abstract K getID(T instance);

    public Codec<T> getLookupCodec() {
        return lookupCodec;
    }

    public Codec<Optional<T>> getLookupOptionalCodec() {
        return lookupOptionalCodec;
    }

    public T get(K key) {
        return data.get(key);
    }

    public Map<K, T> getMap() {
        return data;
    }

    public Collection<T> getValues() {
        return getMap().values();
    }

}
