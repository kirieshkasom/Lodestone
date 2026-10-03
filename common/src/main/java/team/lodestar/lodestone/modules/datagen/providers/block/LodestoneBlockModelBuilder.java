package team.lodestar.lodestone.modules.datagen.providers.block;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import team.lodestar.lodestone.modules.datagen.DatagenSystemCommons;
import team.lodestar.lodestone.modules.datagen.model.ModelFile;
import team.lodestar.lodestone.modules.datagen.providers.ResourceFileHelper;

public class LodestoneBlockModelBuilder extends ModelFile {
    private final ResourceFileHelper helper;

    public LodestoneBlockModelBuilder(ResourceLocation outputLocation) {
        this(outputLocation, ResourceFileHelper.empty());
    }

    public LodestoneBlockModelBuilder(ResourceLocation outputLocation, ResourceFileHelper helper) {
        super(outputLocation, new JsonObject());
        this.helper = helper;
    }

    public LodestoneBlockModelBuilder parent(ModelFile parent) {
        ResourceLocation location = DatagenSystemCommons.modifyModelParentPath(parent.getLocation());
        json.addProperty("parent", location.toString());
        return this;
    }

    public LodestoneBlockModelBuilder texture(String key, ResourceLocation path) {
        ResourceLocation modified = DatagenSystemCommons.modifyTexturePath(path);
        helper.requireTexture(modified);
        DatagenSystemCommons.writeBlockTextureFromBlockModel(key, modified);
        JsonObject textures = json.has("textures") ? json.getAsJsonObject("textures") : new JsonObject();
        textures.addProperty(key, modified.toString());
        json.add("textures", textures);
        return this;
    }

    public LodestoneBlockModelBuilder renderType(String renderType) {
        json.addProperty("render_type", renderType);
        return this;
    }

    public LodestoneBlockModelBuilder renderType(ResourceLocation renderType) {
        return renderType(renderType.toString());
    }
}
