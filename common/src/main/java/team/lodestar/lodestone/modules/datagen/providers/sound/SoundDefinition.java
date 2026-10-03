package team.lodestar.lodestone.modules.datagen.providers.sound;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import team.lodestar.lodestone.modules.datagen.providers.ResourceFileHelper;
import java.util.function.Predicate;

import java.util.ArrayList;
import java.util.List;

public final class SoundDefinition {
    private String subtitle;
    private final List<Sound> sounds = new ArrayList<>();

    public static SoundDefinition definition() {
        return new SoundDefinition();
    }

    public SoundDefinition subtitle(String subtitle) {
        this.subtitle = subtitle;
        return this;
    }

    public SoundDefinition with(Sound... sounds) {
        this.sounds.addAll(List.of(sounds));
        return this;
    }

    public void validate(ResourceFileHelper helper, Predicate<ResourceLocation> eventExists) {
        for (Sound sound : sounds) {
            ResourceLocation id = ResourceLocation.parse(sound.name);
            boolean exists = "event".equals(sound.type) ? eventExists.test(id) : helper.exists(id, PackType.CLIENT_RESOURCES, ".ogg", "sounds");
            if (!exists) {
                throw new IllegalStateException("Missing sound " + ("event".equals(sound.type) ? "event: " : "file: ") + id);
            }
        }
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        if (subtitle != null) {
            json.addProperty("subtitle", subtitle);
        }
        JsonArray entries = new JsonArray();
        for (Sound sound : sounds) {
            entries.add(sound.toJson());
        }
        json.add("sounds", entries);
        return json;
    }

    public static final class Sound {
        private String name;
        private String type;
        private Float volume;
        private Float pitch;
        private Integer weight;
        private Boolean stream;
        private Boolean preload;

        private Sound(ResourceLocation id) {
            this.name = id.toString();
        }

        public static Sound sound(ResourceLocation id) {
            return new Sound(id);
        }

        public Sound type(String type) {
            if (!type.equals("event") && !type.equals("sound")) {
                throw new IllegalArgumentException("Sound type must be sound or event");
            }
            this.type = type;
            return this;
        }

        public Sound volume(float volume) {
            this.volume = volume;
            return this;
        }

        public Sound pitch(float pitch) {
            this.pitch = pitch;
            return this;
        }

        public Sound weight(int weight) {
            this.weight = weight;
            return this;
        }

        public Sound stream() {
            this.stream = true;
            return this;
        }

        public Sound preload() {
            this.preload = true;
            return this;
        }

        private JsonObject toJson() {
            JsonObject json = new JsonObject();
            json.addProperty("name", name);
            if (type != null) {
                json.addProperty("type", type);
            }
            if (volume != null) {
                json.addProperty("volume", volume);
            }
            if (pitch != null) {
                json.addProperty("pitch", pitch);
            }
            if (weight != null) {
                json.addProperty("weight", weight);
            }
            if (stream != null) {
                json.addProperty("stream", stream);
            }
            if (preload != null) {
                json.addProperty("preload", preload);
            }
            return json;
        }
    }
}
