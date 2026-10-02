package team.lodestar.lodestone.modules.datagen.modular;

import com.google.gson.*;
import com.mojang.serialization.*;
import net.minecraft.core.*;
import net.minecraft.resources.*;

public abstract class ModularDatagenJsonBody<T> extends ModularDatagenBody {

    protected ModularDatagenJsonBody(ResourceLocation id) {
        super(id);
    }

    public JsonElement buildJson(RegistryOps<JsonElement> dynamicOps, T instance) {
        return getCodec().encodeStart(dynamicOps, instance).getOrThrow();
    }

    public abstract T build(ModularDatagenProvider datagen, RegistryOps<JsonElement> dynamicOps, HolderLookup.Provider provider);

    public abstract Codec<T> getCodec();
}
