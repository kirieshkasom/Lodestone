package team.lodestar.lodestone.systems.rendering.builder;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

import java.awt.*;
import java.util.function.Supplier;

public class ScreenVFXBuilder extends AbstractVFXBuilder {
    public float x0 = 0, y0 = 0, x1 = 1, y1 = 1;
    public int zLevel;

    Supplier<ShaderInstance> shader;
    ResourceLocation texture;
    Tesselator tesselator = Tesselator.getInstance();
    BufferBuilder builder;
    int placedVertices;

    public ScreenVFXBuilder setShader(Supplier<ShaderInstance> shader) {
        this.shader = shader;
        return updateVertexFormat();
    }

    public ScreenVFXBuilder setShader(ShaderInstance shader) {
        this.shader = () -> shader;
        return updateVertexFormat();
    }

    public Supplier<ShaderInstance> getShader() {
        if (shader == null) {
            setShader(GameRenderer::getPositionTexShader);
        }
        return shader;
    }

    public ScreenVFXBuilder setTexture(ResourceLocation texture) {
        this.texture = texture;
        return this;
    }

    public ScreenVFXBuilder setSprite(ResourceLocation location) {
        var sprite = Minecraft.getInstance().getGuiSprites().getSprite(location);
        this.u0 = sprite.getU0();
        this.v0 = sprite.getV0();
        this.u1 = sprite.getU1();
        this.v1 = sprite.getV1();
        return setTexture(sprite.atlasLocation());
    }

    public final ScreenVFXBuilder updateVertexFormat() {
        return (ScreenVFXBuilder) setFormat(getShader().get().getVertexFormat());
    }

    public ScreenVFXBuilder setLight(int light) {
        this.light = light;
        return this;
    }

    public ScreenVFXBuilder setPositionWithWidth(float x, float y, float width, float height) {
        return setPosition(x, y, x + width, y + height);
    }

    public ScreenVFXBuilder setPosition(float x0, float y0, float x1, float y1) {
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
        return this;
    }

    public ScreenVFXBuilder setZLevel(int z) {
        this.zLevel = z;
        return this;
    }

    public ScreenVFXBuilder blit(GuiGraphics graphics) {
        return blit(graphics.pose());
    }

    public ScreenVFXBuilder blit(PoseStack stack) {
        vertex(stack, x0, y1, u0, v1);
        vertex(stack, x1, y1, u1, v1);
        vertex(stack, x1, y0, u1, v0);
        vertex(stack, x0, y0, u0, v0);
        return this;
    }

    public ScreenVFXBuilder vertex(PoseStack stack, float x, float y, float u, float v) {
        if (builder == null) {
            RenderSystem.setShader(getShader());
            if (texture != null) {
                RenderSystem.setShaderTexture(0, texture);
            }
            builder = tesselator.begin(mode, format);
        }
        vertex(builder, stack.last(), x, y, zLevel, u, v);
        placedVertices++;
        if (placedVertices == 4) {
            BufferUploader.drawWithShader(builder.buildOrThrow());
            placedVertices = 0;
            builder = null;
        }
        return this;
    }

    @Override
    public ScreenVFXBuilder setFormat(VertexFormat format) {
        return (ScreenVFXBuilder) super.setFormat(format);
    }

    @Override
    public ScreenVFXBuilder setColor(int rgba) {
        return (ScreenVFXBuilder) super.setColor(rgba);
    }

    @Override
    public ScreenVFXBuilder setColor(int rgb, int a) {
        return (ScreenVFXBuilder) super.setColor(rgb, a);
    }

    @Override
    public ScreenVFXBuilder setColor(int rgb, float a) {
        return (ScreenVFXBuilder) super.setColor(rgb, a);
    }

    @Override
    public ScreenVFXBuilder setColor(Color color) {
        return (ScreenVFXBuilder) super.setColor(color);
    }

    @Override
    public ScreenVFXBuilder setColor(Color color, int a) {
        return (ScreenVFXBuilder) super.setColor(color, a);
    }

    @Override
    public ScreenVFXBuilder setColor(Color color, float a) {
        return (ScreenVFXBuilder) super.setColor(color, a);
    }

    @Override
    public ScreenVFXBuilder setColor(int r, int g, int b, int a) {
        return (ScreenVFXBuilder) super.setColor(r, g, b, a);
    }

    @Override
    public ScreenVFXBuilder setColor(float r, float g, float b, float a) {
        return (ScreenVFXBuilder) super.setColor(r, g, b, a);
    }

    @Override
    public ScreenVFXBuilder setColor(int r, int g, int b) {
        return (ScreenVFXBuilder) super.setColor(r, g, b);
    }

    @Override
    public ScreenVFXBuilder setColor(float r, float g, float b) {
        return (ScreenVFXBuilder) super.setColor(r, g, b);
    }

    @Override
    public ScreenVFXBuilder multiplyColor(float scalar) {
        return (ScreenVFXBuilder) super.multiplyColor(scalar);
    }

    @Override
    public ScreenVFXBuilder multiplyColor(float r, float g, float b) {
        return (ScreenVFXBuilder) super.multiplyColor(r, g, b);
    }

    @Override
    public ScreenVFXBuilder setAlpha(int a) {
        return (ScreenVFXBuilder) super.setAlpha(a);
    }

    @Override
    public ScreenVFXBuilder setAlpha(float a) {
        return (ScreenVFXBuilder) super.setAlpha(a);
    }

    @Override
    public ScreenVFXBuilder setUVWithWidth(float u, float v, float width, float height, float canvasSize) {
        return (ScreenVFXBuilder) super.setUVWithWidth(u, v, width, height, canvasSize);
    }

    @Override
    public ScreenVFXBuilder setUVWithWidth(float u, float v, float width, float height, float canvasSizeX, float canvasSizeY) {
        return (ScreenVFXBuilder) super.setUVWithWidth(u, v, width, height, canvasSizeX, canvasSizeY);
    }

    @Override
    public ScreenVFXBuilder setUVWithWidth(float u, float v, float width, float height) {
        return (ScreenVFXBuilder) super.setUVWithWidth(u, v, width, height);
    }

    @Override
    public ScreenVFXBuilder setUV(float u0, float v0, float u1, float v1, float canvasSize) {
        return (ScreenVFXBuilder) super.setUV(u0, v0, u1, v1, canvasSize);
    }

    @Override
    public ScreenVFXBuilder setUV(float u0, float v0, float u1, float v1, float canvasSizeX, float canvasSizeY) {
        return (ScreenVFXBuilder) super.setUV(u0, v0, u1, v1, canvasSizeX, canvasSizeY);
    }

    @Override
    public ScreenVFXBuilder setUV(float u0, float v0, float u1, float v1) {
        return (ScreenVFXBuilder) super.setUV(u0, v0, u1, v1);
    }

    @Override
    public ScreenVFXBuilder setNormal(Vector3f normal) {
        return (ScreenVFXBuilder) super.setNormal(normal);
    }
}
