package team.lodestar.lodestone.modules.datagen.modular;

import net.minecraft.resources.*;

public abstract class ModularDatagenBody {
    protected final ResourceLocation id;

    protected ModularDatagenBody(ResourceLocation id) {
        this.id = id;
    }

    public abstract String getDataLocation();

    public ResourceLocation getId() {
        return id;
    }
}
