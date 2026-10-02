package team.lodestar.lodestone.modules.rendering.model.obj;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import team.lodestar.lodestone.modules.rendering.model.IRenderableModel;
import team.lodestar.lodestone.modules.rendering.model.obj.data.*;
import team.lodestar.lodestone.modules.rendering.model.obj.modifier.*;

import java.util.*;

public abstract class IndexedModel implements IRenderableModel {
    protected List<Vertex> vertices;
    protected List<IndexedMesh> meshes;
    protected List<Integer> bakedIndices;
    public List<ModelModifier> earlyModifiers;
    protected List<ModelModifier> modifiers;
    protected ResourceLocation modelId;
    protected VertexBuffer modelBuffer;
    protected MeshData meshData;
    protected Map<String, ObjPart> parts;

    public IndexedModel(ResourceLocation modelId) {
        this.modelId = modelId;
        this.vertices = new ArrayList<>();
        this.meshes = new ArrayList<>();
        this.bakedIndices = new ArrayList<>();
        this.parts = new HashMap<>();
    }

    public void applyModifiers() {
        if (modifiers != null) {
            modifiers.forEach(modifier -> modifier.apply(this));
        }
    }

    public List<Vertex> getVertices() {
        return this.vertices;
    }

    public List<IndexedMesh> getMeshes() {
        return this.meshes;
    }

    public void setMeshes(List<IndexedMesh> meshes) {
        this.meshes = meshes;
    }

    public List<Integer> getBakedIndices() {
        return this.bakedIndices;
    }

    public Map<String, ObjPart> getParts() {
        return this.parts;
    }

    public Optional<ObjPart> getPart(String name) {
        return Optional.ofNullable(this.parts.get(name));
    }

    public List<IndexedMesh> getMeshesForPart(String name) {
        ObjPart part = this.parts.get(name);
        return part == null ? List.of() : part.getMeshes();
    }

    public void bakeIndices(VertexFormat.Mode mode, boolean triangulate) {
        this.bakedIndices.clear();
        this.meshes.stream()
                .filter(mesh -> mesh.indices.size() == mode.primitiveLength)
                .forEach(mesh -> this.bakedIndices.addAll(mesh.indices));

        // TODO: Use triangulation modifier
        for (IndexedMesh mesh : meshes) {
            if (mesh.indices.size() != mode.primitiveLength) {
                if (mesh.indices.size() == 4 && triangulate) {
                    this.bakedIndices.add(mesh.indices.get(0));
                    this.bakedIndices.add(mesh.indices.get(1));
                    this.bakedIndices.add(mesh.indices.get(2));

                    this.bakedIndices.add(mesh.indices.get(2));
                    this.bakedIndices.add(mesh.indices.get(3));
                    this.bakedIndices.add(mesh.indices.get(0));
                } else {
                    this.bakedIndices.addAll(mesh.indices);
                }
            } else {
                this.bakedIndices.addAll(mesh.indices);
            }
        }
    }

    @Override
    public ResourceLocation getModelLocation() {
        return this.modelId;
    }

    @Override
    public void render(PoseStack poseStack, VertexConsumer vertexConsumer, RenderType renderType) {
        for (IndexedMesh mesh : this.meshes) {
            if (mesh.isCompatibleWith(renderType.mode())) {
                for (Vertex vertex : mesh.getVertices(this)) {
                    vertex.supplyVertexData(vertexConsumer, renderType.format(), poseStack);
                }
            }
        }
    }

    @Override
    public MeshData createMesh(PoseStack poseStack, VertexFormat vertexFormat, VertexFormat.Mode mode) {
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(mode, vertexFormat);
        for (IndexedMesh mesh : this.meshes) {
            for (Vertex vertex : mesh.getVertices(this)) {
                vertex.supplyVertexData(bufferBuilder, vertexFormat, poseStack);
            }
        }
        return bufferBuilder.buildOrThrow();
    }

    public void renderPart(String partName, PoseStack poseStack, VertexConsumer vertexConsumer, RenderType renderType) {
        ObjPart part = this.parts.get(partName);
        if (part == null) {
            return;
        }

        for (IndexedMesh mesh : part.getMeshes()) {
            if (mesh.isCompatibleWith(renderType.mode())) {
                for (Vertex vertex : mesh.getVertices(this)) {
                    vertex.supplyVertexData(vertexConsumer, renderType.format(), poseStack);
                }
            }
        }
    }

    @Override
    public VertexBuffer getModelBuffer() {
        return this.modelBuffer;
    }

    @Override
    public void setModelBuffer(VertexBuffer modelBuffer) {
        this.modelBuffer = modelBuffer;
    }

    @Override
    public void cleanup() {
        if (this.modelBuffer != null) {
            this.modelBuffer.close();
        }
    }
}
