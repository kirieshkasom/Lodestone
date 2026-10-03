package team.lodestar.lodestone.fabric.mixin.client;

import com.google.gson.JsonObject;
import net.minecraft.client.renderer.block.model.BlockModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import team.lodestar.lodestone.fabric.rendering.FabricModelData;

@Mixin(BlockModel.class)
public class BlockModelDataMixin implements FabricModelData {
    @Unique
    private JsonObject lodestone$modelData;

    @Override
    public JsonObject lodestone$modelData() {
        return lodestone$modelData;
    }

    @Override
    public void lodestone$modelData(JsonObject data) {
        lodestone$modelData = data;
    }
}
