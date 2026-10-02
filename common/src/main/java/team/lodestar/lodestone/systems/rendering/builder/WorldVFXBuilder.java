package team.lodestar.lodestone.systems.rendering.builder;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import team.lodestar.lodestone.helpers.RenderHelper;
import team.lodestar.lodestone.modules.rendering.LodestoneRenderingSystem;
import team.lodestar.lodestone.systems.rendering.LodestoneRenderLayer;
import team.lodestar.lodestone.systems.rendering.builder.data.CubeVertexData;
import team.lodestar.lodestone.systems.rendering.rendeertype.LodestoneRenderTypeBuilder;
import team.lodestar.lodestone.systems.rendering.trail.TrailPoint;
import team.lodestar.lodestone.systems.rendering.trail.TrailPointBuilder;
import team.lodestar.lodestone.systems.rendering.trail.TrailPointRenderData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("UnusedReturnValue")
public class WorldVFXBuilder extends AbstractVFXBuilder {

    private final Minecraft minecraft = Minecraft.getInstance();

    @Nonnull
    protected LodestoneRenderLayer renderLayer = LodestoneRenderingSystem.DEFERRED_RENDER;

    protected MultiBufferSource bufferSource;
    protected RenderType renderType;
    protected VertexConsumer vertexConsumer;
    protected boolean usePartialTicks;
    protected float partialTicks;

    @Override
    public WorldVFXBuilder setNormal(Vector3f normal) {
        return (WorldVFXBuilder) super.setNormal(normal);
    }

    @Override
    public WorldVFXBuilder setUV(float u0, float v0, float u1, float v1) {
        return (WorldVFXBuilder) super.setUV(u0, v0, u1, v1);
    }

    @Override
    public WorldVFXBuilder setUV(float u0, float v0, float u1, float v1, float canvasSizeX, float canvasSizeY) {
        return (WorldVFXBuilder) super.setUV(u0, v0, u1, v1, canvasSizeX, canvasSizeY);
    }

    @Override
    public WorldVFXBuilder setUV(float u0, float v0, float u1, float v1, float canvasSize) {
        return (WorldVFXBuilder) super.setUV(u0, v0, u1, v1, canvasSize);
    }

    @Override
    public WorldVFXBuilder setUVWithWidth(float u, float v, float width, float height) {
        return (WorldVFXBuilder) super.setUVWithWidth(u, v, width, height);
    }

    @Override
    public WorldVFXBuilder setUVWithWidth(float u, float v, float width, float height, float canvasSizeX, float canvasSizeY) {
        return (WorldVFXBuilder) super.setUVWithWidth(u, v, width, height, canvasSizeX, canvasSizeY);
    }

    @Override
    public WorldVFXBuilder setUVWithWidth(float u, float v, float width, float height, float canvasSize) {
        return (WorldVFXBuilder) super.setUVWithWidth(u, v, width, height, canvasSize);
    }

    @Override
    public WorldVFXBuilder setLight(int light) {
        return (WorldVFXBuilder) super.setLight(light);
    }

    @Override
    public WorldVFXBuilder setAlpha(float a) {
        return (WorldVFXBuilder) super.setAlpha(a);
    }

    @Override
    public WorldVFXBuilder setAlpha(int a) {
        return (WorldVFXBuilder) super.setAlpha(a);
    }

    @Override
    public WorldVFXBuilder multiplyColor(float r, float g, float b) {
        return (WorldVFXBuilder) super.multiplyColor(r, g, b);
    }

    @Override
    public WorldVFXBuilder multiplyColor(float scalar) {
        return (WorldVFXBuilder) super.multiplyColor(scalar);
    }

    @Override
    public WorldVFXBuilder setColor(float r, float g, float b) {
        return (WorldVFXBuilder) super.setColor(r, g, b);
    }

    @Override
    public WorldVFXBuilder setColor(int r, int g, int b) {
        return (WorldVFXBuilder) super.setColor(r, g, b);
    }

    @Override
    public WorldVFXBuilder setColor(float r, float g, float b, float a) {
        return (WorldVFXBuilder) super.setColor(r, g, b, a);
    }

    @Override
    public WorldVFXBuilder setColor(int r, int g, int b, int a) {
        return (WorldVFXBuilder) super.setColor(r, g, b, a);
    }

    @Override
    public WorldVFXBuilder setColor(Color color, float a) {
        return (WorldVFXBuilder) super.setColor(color, a);
    }

    @Override
    public WorldVFXBuilder setColor(Color color, int a) {
        return (WorldVFXBuilder) super.setColor(color, a);
    }

    @Override
    public WorldVFXBuilder setColor(Color color) {
        return (WorldVFXBuilder) super.setColor(color);
    }

    @Override
    public WorldVFXBuilder setColor(int rgb, float a) {
        return (WorldVFXBuilder) super.setColor(rgb, a);
    }

    @Override
    public WorldVFXBuilder setColor(int rgb, int a) {
        return (WorldVFXBuilder) super.setColor(rgb, a);
    }

    @Override
    public WorldVFXBuilder setColor(int rgba) {
        return (WorldVFXBuilder) super.setColor(rgba);
    }

    @Override
    public WorldVFXBuilder setFormat(VertexFormat format) {
        return (WorldVFXBuilder) super.setFormat(format);
    }

    public WorldVFXBuilder setRenderType(LodestoneRenderTypeBuilder renderType) {
        return setRenderType(renderType.getRenderType());
    }

    public WorldVFXBuilder setRenderType(RenderType renderType) {
        return setRenderTypeRaw(renderType).setFormat(renderType.format()).setVertexConsumer(getBufferSource().getBuffer(renderType));
    }

    public WorldVFXBuilder setRenderTypeRaw(RenderType renderType) {
        this.renderType = renderType;
        return this;
    }

    public WorldVFXBuilder setVertexConsumer(VertexConsumer vertexConsumer) {
        this.vertexConsumer = vertexConsumer;
        return this;
    }

    public VertexConsumer getVertexConsumer() {
        if (vertexConsumer == null) {
            setVertexConsumer(getBufferSource().getBuffer(getRenderType()));
        }
        return vertexConsumer;
    }

    public WorldVFXBuilder replaceBufferSource(LodestoneRenderLayer renderLayer) {
        this.renderLayer = renderLayer;
        this.vertexConsumer = null;
        return this;
    }

    public WorldVFXBuilder replaceBufferSource(MultiBufferSource bufferSource) {
        this.bufferSource = bufferSource;
        this.vertexConsumer = null;
        return this;
    }

    protected MultiBufferSource getBufferSource() {
        if (bufferSource == null) {
            replaceBufferSource(renderLayer.getTarget());
        }
        return bufferSource;
    }

    public WorldVFXBuilder usePartialTicks(float partialTicks) {
        this.usePartialTicks = true;
        this.partialTicks = partialTicks;
        return this;
    }

    @SuppressWarnings({"DataFlowIssue", "deprecation"})
    public WorldVFXBuilder setLightLevel(BlockPos pos) {
        ClientLevel level = minecraft.level;
        int light = level.hasChunkAt(pos) ? LevelRenderer.getLightColor(level, pos) : 0;
        return setLight(light);
    }

    protected RenderType getRenderType() {
        return renderType;
    }

    protected VertexFormat getFormat() {
        return format;
    }

    protected Vec3 getCameraPosition() {
        return minecraft.getBlockEntityRenderDispatcher().camera.getPosition();
    }

    protected Matrix4f getOffsetViewMatrix() {
        var pose = new Matrix4f(LodestoneRenderingSystem.MODEL_VIEW);
        var cameraPosition = getCameraPosition().toVector3f();
        return pose.translate(-cameraPosition.x, -cameraPosition.y, -cameraPosition.z);
    }

    public WorldVFXBuilder renderBeam(@Nullable PoseStack.Pose last, BlockPos start, BlockPos end, float width) {
        return renderBeam(last, start.getCenter(), end.getCenter(), width, getCameraPosition());
    }

    public WorldVFXBuilder renderBeam(@Nullable PoseStack.Pose last, Vec3 start, Vec3 end, float width) {
        return renderBeam(last, start, end, width, getCameraPosition());
    }

    public WorldVFXBuilder renderBeam(@Nullable PoseStack.Pose last, Vec3 start, Vec3 end, float width, Consumer<WorldVFXBuilder> consumer) {
        return renderBeam(last, start, end, width, getCameraPosition(), consumer);
    }

    public WorldVFXBuilder renderBeam(@Nullable PoseStack.Pose last, Vec3 start, Vec3 end, float width, Vec3 cameraPosition) {
        return renderBeam(last, start, end, width, cameraPosition, builder -> {
        });
    }

    public WorldVFXBuilder renderBeam(@Nullable PoseStack.Pose last, Vec3 start, Vec3 end, float width, Vec3 cameraPosition, Consumer<WorldVFXBuilder> consumer) {
        Vec3 delta = end.subtract(start);
        Vec3 normal = start.subtract(cameraPosition).cross(delta).normalize().multiply(width / 2f, width / 2f, width / 2f);

        Vec3[] positions = new Vec3[]{start.subtract(normal), start.add(normal), end.add(normal), end.subtract(normal)};

        vertex(getVertexConsumer(), last, (float) positions[0].x, (float) positions[0].y, (float) positions[0].z, u0, v1);
        vertex(getVertexConsumer(), last, (float) positions[1].x, (float) positions[1].y, (float) positions[1].z, u1, v1);
        consumer.accept(this);
        vertex(getVertexConsumer(), last, (float) positions[2].x, (float) positions[2].y, (float) positions[2].z, u1, v0);
        vertex(getVertexConsumer(), last, (float) positions[3].x, (float) positions[3].y, (float) positions[3].z, u0, v0);
        return this;
    }

    public WorldVFXBuilder renderTrail(TrailPointBuilder trailPoints, float width) {
        return renderTrail(trailPoints, f -> width, f -> {
        });
    }

    public WorldVFXBuilder renderTrail(TrailPointBuilder trailPoints, Function<Float, Float> widthFunc) {
        return renderTrail(trailPoints, widthFunc, f -> {
        });
    }

    public WorldVFXBuilder renderTrail(TrailPointBuilder builder, Function<Float, Float> widthFunc, Consumer<Float> vfxOperator) {
        var trailPoints = builder.getTrailPoints();
        if (trailPoints.size() < 2) {
            return this;
        }
        var pose = getOffsetViewMatrix();
        var positions = usePartialTicks ? builder.build(pose, partialTicks) : builder.build(pose);
        positions.getLast().set(TrailPoint.getMatrixPosition(builder.getOrigin(), pose));
        int count = trailPoints.size() - 1;
        float increment = 1.0F / count;
        TrailPointRenderData[] renderData = new TrailPointRenderData[trailPoints.size()];
        for (int i = 1; i < count; i++) {
            float width = widthFunc.apply(increment * i);
            Vector4f previous = positions.get(i - 1);
            Vector4f current = positions.get(i);
            Vector4f next = positions.get(i + 1);
            renderData[i] = new TrailPointRenderData(current, RenderHelper.perpendicularTrailPoints(previous, next, width));
        }
        Vector4f first = positions.get(0);
        Vector4f second = positions.get(1);
        Vector4f secondToLast = positions.get(count - 1);
        Vector4f last = positions.get(count);
        renderData[0] = new TrailPointRenderData(first, RenderHelper.perpendicularTrailPoints(first, second, widthFunc.apply(0f)));
        renderData[count] = new TrailPointRenderData(last, RenderHelper.perpendicularTrailPoints(secondToLast, last, widthFunc.apply(1f)));
        return renderConnectedPoints(renderData, u0, v0, u1, v1, vfxOperator);
    }

    public WorldVFXBuilder renderConnectedPoints(TrailPointRenderData[] points, float u0, float v0, float u1, float v1, Consumer<Float> vfxOperator) {
        replaceBufferSource(renderLayer.getTrailTarget());
        int count = points.length - 1;
        float increment = 1.0F / count;
        vfxOperator.accept(0f);
        points[0].renderStart(getVertexConsumer(), this, u0, v0, u1);
        for (int i = 1; i < count; i++) {
            float current = Mth.lerp(i * increment, v0, v1);
            vfxOperator.accept(current);
            points[i].renderMid(getVertexConsumer(), this, u0, current, u1, current);
        }
        vfxOperator.accept(1f);
        points[count].renderEnd(getVertexConsumer(), this, u0, u1, v1);
        return this;
    }

    public WorldVFXBuilder renderCube(PoseStack poseStack, CubeVertexData cubeVertexData) {
        var topVertices = cubeVertexData.topVertices().vertices();
        var bottomVertices = cubeVertexData.bottomVertices().invert();

        renderQuad(poseStack, topVertices);
        renderQuad(poseStack, bottomVertices);
        for (CubeVertexData.CubeVertices horizontal : cubeVertexData.byHorizontalDirection()) {
            renderQuad(poseStack, horizontal.vertices());
        }
        return this;
    }

    public WorldVFXBuilder renderCubeSides(PoseStack poseStack, CubeVertexData cubeVertexData, Direction... directions) {
        return renderCubeSides(poseStack, cubeVertexData, Arrays.asList(directions));
    }

    public WorldVFXBuilder renderCubeSides(PoseStack poseStack, CubeVertexData cubeVertexData, Collection<Direction> directions) {
        for (Direction direction : directions) {
            renderCubeSide(poseStack, cubeVertexData, direction);
        }
        return this;
    }

    public WorldVFXBuilder renderCubeSide(PoseStack poseStack, CubeVertexData cubeVertexData, Direction direction) {
        Vector3f[] vertices = cubeVertexData.getVerticesByDirection(direction);
        renderQuad(poseStack, vertices);
        return this;
    }

    public WorldVFXBuilder renderQuad(PoseStack stack) {
        return renderQuad(stack, 1f);
    }

    public WorldVFXBuilder renderQuad(PoseStack stack, float size) {
        return renderQuad(stack, size, size);
    }

    public WorldVFXBuilder renderQuad(PoseStack stack, float width, float height) {
        Vector3f[] positions = new Vector3f[]{new Vector3f(-1, -1, 0), new Vector3f(1, -1, 0), new Vector3f(1, 1, 0), new Vector3f(-1, 1, 0)};
        return renderQuad(stack, positions, width, height);
    }

    public WorldVFXBuilder renderQuad(PoseStack stack, Vector3f[] positions, float size) {
        return renderQuad(stack, positions, size, size);
    }

    public WorldVFXBuilder renderQuad(PoseStack stack, Vector3f[] positions, float width, float height) {
        for (Vector3f position : positions) {
            position.mul(width, height, width);
        }
        return renderQuad(stack, positions);
    }

    public WorldVFXBuilder renderQuad(PoseStack stack, Vector3f[] positions) {
        var consumer = getVertexConsumer();
        var pose = stack.last();
        vertex(consumer, pose, positions[0].x(), positions[0].y(), positions[0].z(), u0, v1);
        vertex(consumer, pose, positions[1].x(), positions[1].y(), positions[1].z(), u1, v1);
        vertex(consumer, pose, positions[2].x(), positions[2].y(), positions[2].z(), u1, v0);
        vertex(consumer, pose, positions[3].x(), positions[3].y(), positions[3].z(), u0, v0);
        return this;
    }

    /**
     * RenderSphere requires a triangle-based RenderType.
     */
    public WorldVFXBuilder renderSphere(PoseStack stack, float radius, int longs, int lats) {
        float startU = u0;
        float startV = v0;
        float endU = Mth.PI * 2 * u1;
        float endV = Mth.PI * v1;
        float stepU = (endU - startU) / longs;
        float stepV = (endV - startV) / lats;
        for (int i = 0; i < longs; ++i) {
            // U-points
            for (int j = 0; j < lats; ++j) {
                // V-points
                float u = i * stepU + startU;
                float v = j * stepV + startV;
                float un = (i + 1 == longs) ? endU : (i + 1) * stepU + startU;
                float vn = (j + 1 == lats) ? endV : (j + 1) * stepV + startV;
                Vector3f p0 = RenderHelper.parametricSphere(u, v, radius);
                Vector3f p1 = RenderHelper.parametricSphere(u, vn, radius);
                Vector3f p2 = RenderHelper.parametricSphere(un, v, radius);
                Vector3f p3 = RenderHelper.parametricSphere(un, vn, radius);

                float textureU = u / endU * radius;
                float textureV = v / endV * radius;
                float textureUN = un / endU * radius;
                float textureVN = vn / endV * radius;
                var pose = stack.last();
                vertex(getVertexConsumer(), pose, p0.x(), p0.y(), p0.z(), textureU, textureV);
                vertex(getVertexConsumer(), pose, p2.x(), p2.y(), p2.z(), textureUN, textureV);
                vertex(getVertexConsumer(), pose, p1.x(), p1.y(), p1.z(), textureU, textureVN);

                vertex(getVertexConsumer(), pose, p3.x(), p3.y(), p3.z(), textureUN, textureVN);
                vertex(getVertexConsumer(), pose, p1.x(), p1.y(), p1.z(), textureU, textureVN);
                vertex(getVertexConsumer(), pose, p2.x(), p2.y(), p2.z(), textureUN, textureV);
            }
        }
        return this;
    }

    /**
     * RenderTorus requires a triangle-based RenderType.
     */
    public WorldVFXBuilder renderTorus(PoseStack stack, float majorRadius, float minorRadius, int majorSegments, int minorSegments) {
        float TAU = (float) (Math.PI * 2);

        for (int i = 0; i < majorSegments; ++i) {
            float u0n = (float) i / majorSegments;
            float u1n = (float) (i + 1) / majorSegments;
            float u0 = u0n * TAU;
            float u1 = u1n * TAU;

            for (int j = 0; j < minorSegments; ++j) {
                float v0n = (float) j / minorSegments;
                float v1n = (float) (j + 1) / minorSegments;
                float v0 = v0n * TAU;
                float v1 = v1n * TAU;

                Vector3f p0 = RenderHelper.parametricTorus(u0, v0, majorRadius, minorRadius);
                Vector3f p1 = RenderHelper.parametricTorus(u0, v1, majorRadius, minorRadius);
                Vector3f p2 = RenderHelper.parametricTorus(u1, v0, majorRadius, minorRadius);
                Vector3f p3 = RenderHelper.parametricTorus(u1, v1, majorRadius, minorRadius);

                var pose = stack.last();
                vertex(getVertexConsumer(), pose, p0.x(), p0.y(), p0.z(), u0n, v0n);
                vertex(getVertexConsumer(), pose, p2.x(), p2.y(), p2.z(), u1n, v0n);
                vertex(getVertexConsumer(), pose, p1.x(), p1.y(), p1.z(), u0n, v1n);

                vertex(getVertexConsumer(), pose, p3.x(), p3.y(), p3.z(), u1n, v1n);
                vertex(getVertexConsumer(), pose, p1.x(), p1.y(), p1.z(), u0n, v1n);
                vertex(getVertexConsumer(), pose, p2.x(), p2.y(), p2.z(), u1n, v0n);
            }
        }
        return this;
    }
}
