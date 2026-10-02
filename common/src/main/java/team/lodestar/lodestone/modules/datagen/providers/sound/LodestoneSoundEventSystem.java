package team.lodestar.lodestone.modules.datagen.providers.sound;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.sounds.SoundEvent;
import team.lodestar.lodestone.modules.datagen.providers.ResourceFileHelper;
import com.google.gson.JsonObject;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public abstract class LodestoneSoundEventSystem implements DataProvider {
    public static LodestoneSoundEventSystem INSTANCE;
    public final String modId;
    public final ResourceFileHelper helper;
    private final Map<ResourceLocation, SoundDefinition> definitions = new LinkedHashMap<>();
    private final PackOutput output;
    private final String providerName;

    public LodestoneSoundEventSystem(PackOutput output, String modId, ResourceFileHelper helper) {
        this.output = output;
        this.providerName = modId + " Sound Definitions";
        this.modId = modId;
        this.helper = helper;
        INSTANCE = this;
    }

    public SoundDefinition definition(SoundEvent event) {
        return SoundDefinition.definition().subtitle(subtitle(event));
    }

    public void add(String event, SoundDefinition definition) {
        add(ResourceLocation.fromNamespaceAndPath(modId, event), definition);
    }

    public void add(Supplier<SoundEvent> event, SoundDefinition definition) {
        add(event.get(), definition);
    }

    public void add(SoundEvent event, SoundDefinition definition) {
        add(event.getLocation(), definition);
    }

    public void add(ResourceLocation event, SoundDefinition definition) {
        definitions.put(event, definition);
    }

    @SafeVarargs
    public final SoundDefinition add(Supplier<SoundEvent> event, Consumer<SoundDefinition>... modifiers) {
        SoundDefinition definition = definition(event.get());
        for (Consumer<SoundDefinition> modifier : modifiers) {
            modifier.accept(definition);
        }
        add(event, definition);
        return definition;
    }

    public static SoundDefinition.Sound sound(String name) {
        ResourceLocation id = name.contains(":") ? ResourceLocation.parse(name) : ResourceLocation.fromNamespaceAndPath(INSTANCE.modId, name);
        return sound(id);
    }

    public SoundDefinition.Sound[] sounds(String name, int variants) {
        SoundDefinition.Sound[] result = new SoundDefinition.Sound[variants];
        for (int index = 0; index < variants; index++) {
            String id = name + (index + 1);
            ResourceLocation location = name.contains(":") ? ResourceLocation.parse(id) : ResourceLocation.fromNamespaceAndPath(modId, id);
            result[index] = sound(location);
        }
        return result;
    }

    public SoundDefinition.Sound[] sounds(String name, int variants, Consumer<SoundDefinition.Sound> modifier) {
        SoundDefinition.Sound[] result = sounds(name, variants);
        for (SoundDefinition.Sound sound : result) {
            modifier.accept(sound);
        }
        return result;
    }

    public SoundDefinition.Sound[] allSounds(String path, Consumer<SoundDefinition.Sound> modifier) {
        SoundDefinition.Sound[] result = allSounds(path);
        for (SoundDefinition.Sound sound : result) {
            modifier.accept(sound);
        }
        return result;
    }

    public SoundDefinition.Sound[] allSounds(String basePath, String name, Consumer<SoundDefinition.Sound> modifier, String... fallbacks) {
        SoundDefinition.Sound[] result = allSounds(basePath, name, fallbacks);
        for (SoundDefinition.Sound sound : result) {
            modifier.accept(sound);
        }
        return result;
    }

    public SoundDefinition.Sound[] allSounds(String path) {
        int index = path.lastIndexOf('/');
        return allSounds(path.substring(0, index), path.substring(index + 1));
    }

    public SoundDefinition.Sound[] allSounds(String basePath, String name, String... fallbacks) {
        List<SoundDefinition.Sound> sounds = new ArrayList<>();
        List<String> remainingFallbacks = new ArrayList<>(List.of(fallbacks));
        String prefix = basePath.isEmpty() || basePath.endsWith("/") ? basePath : basePath + "/";
        int counter = 1;
        while (true) {
            String candidate = prefix + name + counter;
            ResourceLocation id = candidate.contains(":") ? ResourceLocation.parse(candidate) : ResourceLocation.fromNamespaceAndPath(modId, candidate);
            if (helper.exists(id, PackType.CLIENT_RESOURCES, ".ogg", "sounds")) {
                sounds.add(sound(id));
                counter++;
            } else if (counter == 1 && !remainingFallbacks.isEmpty()) {
                name = remainingFallbacks.remove(0);
            } else {
                break;
            }
        }
        if (sounds.isEmpty()) {
            throw new UnsupportedOperationException("Sound Definition is empty for sound: " + prefix + name);
        }
        return sounds.toArray(SoundDefinition.Sound[]::new);
    }

    public static SoundDefinition.Sound sound(String namespace, String id) {
        return sound(id.contains(":") ? ResourceLocation.parse(id) : ResourceLocation.fromNamespaceAndPath(namespace, id));
    }

    public static SoundDefinition.Sound sound(ResourceLocation id) {
        return SoundDefinition.Sound.sound(id);
    }

    public String subtitle(SoundEvent event) {
        return subtitle(event.getLocation());
    }

    public String subtitle(ResourceLocation id) {
        return modId + ".subtitle." + id.getPath();
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        Map<String, JsonObject> byNamespace = new LinkedHashMap<>();
        definitions.forEach((id, definition) -> {
            JsonObject namespaceDefinitions = byNamespace.computeIfAbsent(id.getNamespace(), ignored -> new JsonObject());
            namespaceDefinitions.add(id.getPath(), definition.toJson());
        });
        List<CompletableFuture<?>> writes = new ArrayList<>();
        for (Map.Entry<String, JsonObject> entry : byNamespace.entrySet()) {
            Path path = this.output.getOutputFolder(PackOutput.Target.RESOURCE_PACK).resolve(entry.getKey()).resolve("sounds.json");
            writes.add(DataProvider.saveStable(output, entry.getValue(), path));
        }
        return CompletableFuture.allOf(writes.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return providerName;
    }
}
