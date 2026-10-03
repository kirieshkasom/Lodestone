package team.lodestar.lodestone.modules.datagen.providers.item;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import team.lodestar.lodestone.modules.datagen.DatagenSystemCommons;
import team.lodestar.lodestone.modules.datagen.model.ModelFile;
import java.util.function.UnaryOperator;

public final class LodestoneItemModelBuilder extends ModelFile {
    public final LodestoneItemModelSystem provider;
    private UnaryOperator<JsonObject> loaderData = UnaryOperator.identity();

    public LodestoneItemModelBuilder(LodestoneItemModelSystem provider, ResourceLocation outputLocation) {
        super(outputLocation, new JsonObject());
        this.provider = provider;
    }

    public LodestoneItemModelBuilder texture(String key, ResourceLocation path) {
        ResourceLocation modified = DatagenSystemCommons.modifyTexturePath(path);
        provider.helper().requireTexture(modified);
        JsonObject textures = json.has("textures") ? json.getAsJsonObject("textures") : new JsonObject();
        textures.addProperty(key, modified.toString());
        json.add("textures", textures);
        return this;
    }

    public LodestoneItemModelBuilder parent(ModelFile parent) {
        ResourceLocation modified = DatagenSystemCommons.modifyModelParentPath(parent.getLocation());
        json.addProperty("parent", modified.toString());
        return this;
    }

    public void customLoader(ResourceLocation loader, UnaryOperator<JsonObject> data) {
        json.addProperty("loader", loader.toString());
        loaderData = data;
    }

    @Override
    public JsonObject toJson() {
        return loaderData.apply(super.toJson());
    }

    public LodestoneItemModelBuilder display(String transform, JsonObject value) {
        JsonObject display = json.has("display") ? json.getAsJsonObject("display") : new JsonObject();
        display.add(transform, value.deepCopy());
        json.add("display", display);
        return this;
    }
}
