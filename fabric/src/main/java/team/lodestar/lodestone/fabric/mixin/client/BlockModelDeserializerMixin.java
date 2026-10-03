package team.lodestar.lodestone.fabric.mixin.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonDeserializationContext;
import net.minecraft.client.renderer.block.model.BlockModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import team.lodestar.lodestone.fabric.rendering.FabricModelData;

import java.lang.reflect.Type;

@Mixin(BlockModel.Deserializer.class)
public class BlockModelDeserializerMixin {
    @Inject(method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/client/renderer/block/model/BlockModel;", at = @At("RETURN"))
    private void lodestone$modelData(JsonElement element, Type type, JsonDeserializationContext context, CallbackInfoReturnable<BlockModel> callback) {
        JsonObject json = element.getAsJsonObject();
        if (json.has("loader")) {
            String loader = json.get("loader").getAsString();
            if (loader.equals("neoforge:separate_transforms") || loader.equals("neoforge:item_layers")) {
                ((FabricModelData) callback.getReturnValue()).lodestone$modelData(json.deepCopy());
            }
        }
    }
}
