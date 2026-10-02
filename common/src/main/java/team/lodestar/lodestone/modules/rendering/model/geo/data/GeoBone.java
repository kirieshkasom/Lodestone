package team.lodestar.lodestone.modules.rendering.model.geo.data;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;

public class GeoBone implements IRenderableModelPart<GeoBone> {
    private Vector3f position = new Vector3f(0.0f, 0.0f, 0.0f);
    private Vector3f offset = new Vector3f(0.0f, 0.0f, 0.0f);
    private float xRot;
    private float yRot;
    private float zRot;
    private Vector3f scale = new Vector3f(1.0f, 1.0f, 1.0f);
    private final List<GeoCube> cubes;
    private final List<IRenderableModelPart<?>> customParts = new ArrayList<>();
    private final Map<String, GeoBone> children;
    private final String parent;
    private boolean isHidden = false;

    public GeoBone(List<GeoCube> cubes, Map<String, GeoBone> children, String parent) {
        this.cubes = cubes;
        this.children = children;
        this.parent = parent;
    }

    public void copyTransformFrom(GeoBone geoBone) {
        this.scale.x = geoBone.scale.x;
        this.scale.y = geoBone.scale.y;
        this.scale.z = geoBone.scale.z;
        this.xRot = geoBone.xRot;
        this.yRot = geoBone.yRot;
        this.zRot = geoBone.zRot;
        this.offset.x = geoBone.offset.x;
        this.offset.y = geoBone.offset.y;
        this.offset.z = geoBone.offset.z;
        this.position.x = geoBone.position.x;
        this.position.y = geoBone.position.y;
        this.position.z = geoBone.position.z;
    }

    public boolean hasChild(String name) {
        return this.children.containsKey(name);
    }

    public GeoBone getChild(String name) {
        GeoBone bone = this.children.get(name);
        if (bone == null) {
            throw new NoSuchElementException("Can't find bone " + name);
        }
        return bone;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void setPosition(float x, float y, float z) {
        this.position.set(x, y, z);
    }

    public void setRotation(float xDeg, float yDeg, float zDeg) {
        this.xRot = (float)Math.toRadians(xDeg);
        this.yRot = (float)Math.toRadians(yDeg);
        this.zRot = (float)Math.toRadians(zDeg);
    }

    public void setOffset(float x, float y, float z) {
        this.offset.set(x, y, z);
    }

    public void setScale(Vector3f scale) {
        this.scale = scale;
    }

    public void setScale(float x, float y, float z) {
        this.scale.set(x, y, z);
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getOffset() {
        return this.offset;
    }

    public float getxRot() {
        return xRot;
    }

    public float getyRot() {
        return yRot;
    }

    public float getzRot() {
        return zRot;
    }

    public Vector3f getScale() {
        return scale;
    }

    public List<GeoCube> getCubes() {
        return cubes;
    }

    public Map<String, GeoBone> getChildren() {
        return children;
    }

    public String getParent() {
        return parent;
    }

    public void addChild(String name, GeoBone bone) {
        this.children.put(name, bone);
    }

    public void setHidden(boolean hidden) {
        this.isHidden = hidden;
    }

    public boolean isHidden() {
        return this.isHidden;
    }

    public void addCustomPart(IRenderableModelPart<?> part) {
        this.customParts.add(part);
    }

    public void removeCustomPart(IRenderableModelPart<?> part) {
        this.customParts.remove(part);
    }

    public List<IRenderableModelPart<?>> getCustomParts() {
        return this.customParts;
    }

    @Override
    public void render(PoseStack poseStack, VertexConsumer vertexConsumer, VertexFormat vertexFormat, VertexFormat.Mode mode) {
        if (this.isHidden) return;
        if (this.cubes.isEmpty() && this.children.isEmpty() && this.customParts.isEmpty()) return;

        poseStack.pushPose();
        this.translateAndRotate(poseStack);
        poseStack.translate(this.offset.x / 16.0f, this.offset.y / 16.0f, this.offset.z / 16.0f);

        for (GeoCube cube : this.cubes) {
            cube.render(poseStack, vertexConsumer, vertexFormat, mode);
        }

        for (IRenderableModelPart<?> customPart : this.customParts) {
            if (!(customPart instanceof IBatchedRenderableModelPart<?>)) {
                customPart.render(poseStack, vertexConsumer, vertexFormat, mode);
            }
        }

        for (GeoBone child : this.children.values()) {
            child.render(poseStack, vertexConsumer, vertexFormat, mode);
        }
        poseStack.popPose();
    }

    public void renderBatchedCustomParts(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource) {
        if (this.isHidden) return;

        if (this.children.isEmpty() && this.customParts.isEmpty()) return;

        poseStack.pushPose();
        this.translateAndRotate(poseStack);
        poseStack.translate(this.offset.x / 16.0f, this.offset.y / 16.0f, this.offset.z / 16.0f);

        for (IRenderableModelPart<?> customPart : this.customParts) {
            if (customPart instanceof IBatchedRenderableModelPart<?> batchedPart) {
                batchedPart.renderBatched(poseStack, bufferSource);
            }
        }

        for (GeoBone child : this.children.values()) {
            child.renderBatchedCustomParts(poseStack, bufferSource);
        }

        poseStack.popPose();
    }

    public void translateAndRotate(PoseStack poseStack) {
        float px = -this.position.x / 16.0F;
        float py = this.position.y / 16.0F;
        float pz = this.position.z / 16.0F;

        poseStack.translate(px, py, pz);

        if (this.xRot != 0.0F || this.yRot != 0.0F || this.zRot != 0.0F) {
            poseStack.mulPose(new Quaternionf().rotationZYX(-this.zRot, this.yRot, this.xRot));
        }

        poseStack.translate(-px, -py, -pz);

        if (this.scale.x != 1.0F || this.scale.y != 1.0F || this.scale.z != 1.0F) {
            poseStack.scale(this.scale.x, this.scale.y, this.scale.z);
        }
    }

    /**
     * A copy of the GeoBone with its own copies of the original children and cubes.
     */
    @Override
    public GeoBone copy() {
        List<GeoCube> copiedCubes = new ArrayList<>();
        for (GeoCube cube : this.cubes) {
            copiedCubes.add(cube.copy());
        }

        Map<String, GeoBone> copiedChildren = new HashMap<>();
        for (Map.Entry<String, GeoBone> entry : this.children.entrySet()) {
            copiedChildren.put(entry.getKey(), entry.getValue().copy());
        }

        GeoBone copiedBone = new GeoBone(copiedCubes, copiedChildren, this.parent);
        copiedBone.copyTransformFrom(this);

        for (IRenderableModelPart<?> part : this.customParts) {
            copiedBone.addCustomPart((IRenderableModelPart<?>) part.copy());
        }

        return copiedBone;
    }

    /**
     * A copy of the GeoBone with its own copies of the original children but the same cubes.
     * Used if you want to copy the transforms of a bone hierarchy without duplicating cube data.
     */
    public GeoBone minimalCopy() {
        Map<String, GeoBone> copiedChildren = new HashMap<>();
        for (Map.Entry<String, GeoBone> entry : this.children.entrySet()) {
            copiedChildren.put(entry.getKey(), entry.getValue().minimalCopy());
        }

        GeoBone copiedBone = new GeoBone(this.cubes, copiedChildren, this.parent);
        copiedBone.copyTransformFrom(this);

        for (IRenderableModelPart<?> part : this.customParts) {
            copiedBone.addCustomPart((IRenderableModelPart<?>) part.copy());
        }

        return copiedBone;
    }
}