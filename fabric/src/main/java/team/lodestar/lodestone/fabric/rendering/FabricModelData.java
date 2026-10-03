package team.lodestar.lodestone.fabric.rendering;

import com.google.gson.JsonObject;

public interface FabricModelData {
    JsonObject lodestone$modelData();
    void lodestone$modelData(JsonObject data);
}
