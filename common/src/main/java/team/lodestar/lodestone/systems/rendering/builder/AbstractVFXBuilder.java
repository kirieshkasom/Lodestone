package team.lodestar.lodestone.systems.rendering.builder;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Vector3f;
import team.lodestar.lodestone.helpers.RenderHelper;

import java.awt.*;

import static com.mojang.blaze3d.vertex.VertexFormatElement.*;

public abstract class AbstractVFXBuilder {

    float r = 1, g = 1, b = 1, a = 1;
    int light = RenderHelper.FULL_BRIGHT;
    float u0 = 0, v0 = 0, u1 = 1, v1 = 1;
    Vector3f normal;
    VertexFormat format;
    VertexFormat.Mode mode = VertexFormat.Mode.QUADS;

    public AbstractVFXBuilder setFormat(VertexFormat format) {
        this.format = format;
        return this;
    }

    public AbstractVFXBuilder setColor(int rgba) {
        return setColor((rgba >> 16) & 0xFF, (rgba >> 8) & 0xFF, rgba & 0xFF, (rgba >> 24) & 0xFF);
    }

    public AbstractVFXBuilder setColor(int rgb, int a) {
        return setColor((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF, a);
    }

    public AbstractVFXBuilder setColor(int rgb, float a) {
        return setColor((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF).setAlpha(a);
    }

    public AbstractVFXBuilder setColor(Color color) {
        return setColor(color.getRed(), color.getGreen(), color.getBlue());
    }

    public AbstractVFXBuilder setColor(Color color, int a) {
        return setColor(color).setAlpha(a);
    }

    public AbstractVFXBuilder setColor(Color color, float a) {
        return setColor(color).setAlpha(a);
    }

    public AbstractVFXBuilder setColor(int r, int g, int b, int a) {
        return setColor(r, g, b).setAlpha(a);
    }

    public AbstractVFXBuilder setColor(float r, float g, float b, float a) {
        return setColor(r, g, b).setAlpha(a);
    }

    public AbstractVFXBuilder setColor(int r, int g, int b) {
        return setColor(r / 255f, g / 255f, b / 255f);
    }

    public AbstractVFXBuilder setColor(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
        return this;
    }

    public AbstractVFXBuilder multiplyColor(float scalar) {
        return multiplyColor(scalar, scalar, scalar);
    }

    public AbstractVFXBuilder multiplyColor(float r, float g, float b) {
        return setColor(this.r * r, this.g * g, this.b * b);
    }

    public AbstractVFXBuilder setAlpha(int a) {
        return setAlpha(a / 255f);
    }

    public AbstractVFXBuilder setAlpha(float a) {
        this.a = a;
        return this;
    }

    public AbstractVFXBuilder setLight(int light) {
        this.light = light;
        return this;
    }

    public AbstractVFXBuilder setUVWithWidth(float u, float v, float width, float height, float canvasSize) {
        return setUVWithWidth(u, v, width, height, canvasSize, canvasSize);
    }

    public AbstractVFXBuilder setUVWithWidth(float u, float v, float width, float height, float canvasSizeX, float canvasSizeY) {
        return setUVWithWidth(u / canvasSizeX, v / canvasSizeY, width / canvasSizeX, height / canvasSizeY);
    }

    public AbstractVFXBuilder setUVWithWidth(float u, float v, float width, float height) {
        this.u0 = u;
        this.v0 = v;
        this.u1 = (u + width);
        this.v1 = (v + height);
        return this;
    }

    public AbstractVFXBuilder setUV(float u0, float v0, float u1, float v1, float canvasSize) {
        return setUV(u0, v0, u1, v1, canvasSize, canvasSize);
    }

    public AbstractVFXBuilder setUV(float u0, float v0, float u1, float v1, float canvasSizeX, float canvasSizeY) {
        return setUV(u0 / canvasSizeX, v0 / canvasSizeY, u1 / canvasSizeX, v1 / canvasSizeY);
    }

    public AbstractVFXBuilder setUV(float u0, float v0, float u1, float v1) {
        this.u0 = u0;
        this.v0 = v0;
        this.u1 = u1;
        this.v1 = v1;
        return this;
    }

    public AbstractVFXBuilder setNormal(Vector3f normal) {
        this.normal = normal;
        return this;
    }

    public void vertex(VertexConsumer consumer, float x, float y, float z, float u, float v) {
        vertex(consumer, null, x, y, z, u, v);
    }

    public void vertex(VertexConsumer consumer, @Nullable PoseStack.Pose pose, float x, float y, float z, float u, float v) {
        for (VertexFormatElement element : format.getElements()) {
            vertex(element, consumer, pose, x, y, z, u, v);
        }
    }

    private void vertex(VertexFormatElement element, VertexConsumer consumer, @Nullable PoseStack.Pose pose, float x, float y, float z, float u, float v) {
        switch (element.id()) {
            case 0 -> {
                if (pose == null) {
                    consumer.addVertex(x, y, z);
                } else {
                    consumer.addVertex(pose, x, y, z);
                }
            }

            case 1 -> consumer.setColor(r, g, b, a);
            case 2 -> consumer.setUv(u, v);
            case 4 -> consumer.setLight(light);
            case 5 -> {
                if (normal == null) {
                    return;
                }

                if (pose == null) {
                    consumer.setNormal(normal.x, normal.y, normal.z);
                } else {
                    consumer.setNormal(pose, normal.x, normal.y, normal.z);
                }
            }

            default -> {
            }
        }
    }
}
