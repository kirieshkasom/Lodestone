package team.lodestar.lodestone.modules.datagen.model;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

public class ModelFile {
    private final ResourceLocation location;
    protected final JsonObject json;

    public ModelFile(ResourceLocation location) {
        this(location, null);
    }

    protected ModelFile(ResourceLocation location, JsonObject json) {
        this.location = location;
        this.json = json;
    }

    public ResourceLocation getLocation() {
        return location;
    }

    public JsonObject toJson() {
        return json == null ? new JsonObject() : json.deepCopy();
    }

    public static final class ExistingModelFile extends ModelFile {
        public ExistingModelFile(ResourceLocation location) {
            super(location);
        }
    }

    public static final class UncheckedModelFile extends ModelFile {
        public UncheckedModelFile(ResourceLocation location) {
            super(location);
        }
    }
}
