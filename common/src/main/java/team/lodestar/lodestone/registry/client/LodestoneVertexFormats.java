package team.lodestar.lodestone.registry.client;

import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;

public class LodestoneVertexFormats {
    public static VertexFormatElement TANGENT = VertexFormatElement.register(nextElementId(), 0, VertexFormatElement.Type.BYTE, VertexFormatElement.Usage.NORMAL, 3);
    public static VertexFormatElement BITANGENT = VertexFormatElement.register(nextElementId(), 0, VertexFormatElement.Type.BYTE, VertexFormatElement.Usage.NORMAL, 3);
    public static VertexFormatElement SIZE2 = VertexFormatElement.register(nextElementId(), 0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.POSITION, 2);

    private static int nextElementId() {
        for (int id = 0; id < VertexFormatElement.MAX_COUNT; id++) {
            if (VertexFormatElement.byId(id) == null) {
                return id;
            }
        }
        throw new IllegalStateException("No vertex format element IDs remain");
    }
}
