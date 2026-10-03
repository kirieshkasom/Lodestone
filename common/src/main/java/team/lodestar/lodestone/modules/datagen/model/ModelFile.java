package team.lodestar.lodestone.modules.datagen.model;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import team.lodestar.lodestone.modules.datagen.providers.ResourceFileHelper;

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
        private final ResourceFileHelper helper;

        public ExistingModelFile(ResourceLocation location) {
            this(location, ResourceFileHelper.empty());
        }

        public ExistingModelFile(ResourceLocation location, ResourceFileHelper helper) {
            super(location);
            this.helper = helper;
        }

        @Override
        public ResourceLocation getLocation() {
            ResourceLocation location = super.getLocation();
            helper.requireModel(location);
            return location;
        }
    }

    public static final class UncheckedModelFile extends ModelFile {
        public UncheckedModelFile(ResourceLocation location) {
            super(location);
        }
    }
}
