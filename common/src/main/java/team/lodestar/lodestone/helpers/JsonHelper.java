package team.lodestar.lodestone.helpers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.joml.Vector2f;
import org.joml.Vector3f;

import javax.annotation.Nullable;

public class JsonHelper {
    @Nullable
    public static Vector2f getAsVec2f(JsonObject object, String key) {
        JsonArray array = object.getAsJsonArray(key);
        if (array == null || array.size() < 2) return null;
        return new Vector2f(array.get(0).getAsFloat(), array.get(1).getAsFloat());
    }

    @Nullable
    public static Vector3f getAsVec3f(JsonObject object, String key) {
        JsonArray array = object.getAsJsonArray(key);
        if (array == null || array.size() < 3) return null;
        return new Vector3f(array.get(0).getAsFloat(), array.get(1).getAsFloat(), array.get(2).getAsFloat());
    }
}
