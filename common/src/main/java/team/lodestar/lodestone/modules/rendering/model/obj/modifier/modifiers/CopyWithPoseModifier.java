package team.lodestar.lodestone.modules.rendering.model.obj.modifier.modifiers;

import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import team.lodestar.lodestone.modules.rendering.model.obj.IndexedModel;
import team.lodestar.lodestone.modules.rendering.model.obj.ObjParser;
import team.lodestar.lodestone.modules.rendering.model.obj.data.IndexedMesh;
import team.lodestar.lodestone.modules.rendering.model.obj.data.IndexedVertex;
import team.lodestar.lodestone.modules.rendering.model.obj.modifier.ModelModifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class CopyWithPoseModifier extends ModelModifier {
    private static final Map<ObjParser.Builder, OriginalCopy> originalCopies = new HashMap<>();
    private final Consumer<PoseStack> poseStackConsumer;

    public CopyWithPoseModifier(Consumer<PoseStack> poseConsumer) {
        this.poseStackConsumer = poseConsumer;
    }

    @Override
    public void applyEarly(ObjParser.Builder parsedModel) {
        OriginalCopy originalCopy = originalCopies.computeIfAbsent(parsedModel, OriginalCopy::new);

        final int vertexCountBefore = originalCopy.getIndexedVertices().size();
        for (int i = 0; i < vertexCountBefore; i++) {
            IndexedVertex vertex = parsedModel.getIndexedVertices().get(i);
            int posRefIndex = vertex.positionIndex();
            int uvRefIndex = vertex.uvIndex();
            int normalRefIndex = vertex.normalIndex();

            int posSize = parsedModel.getPositions().size();
            int uvSize = parsedModel.getUvs().size();
            int normalSize = parsedModel.getNormals().size();

            Vector3f posCopy = new Vector3f(originalCopy.getPositions().get(posRefIndex));
            Vector2f uvCopy = new Vector2f(originalCopy.getUvs().get(uvRefIndex));
            Vector3f normalCopy = new Vector3f(originalCopy.getNormals().get(normalRefIndex));

            // Apply PoseStack transforms to the copied vertices
            PoseStack poseStack = new PoseStack();
            poseStackConsumer.accept(poseStack);
            PoseStack.Pose pose = poseStack.last();
            Vector4f posCopy4 = new Vector4f(posCopy, 1);
            posCopy4.mul(pose.pose());
            posCopy.set(posCopy4.x(), posCopy4.y(), posCopy4.z());
            normalCopy.mul(pose.normal());

            parsedModel.addPosition(posCopy);
            parsedModel.addUv(uvCopy);
            parsedModel.addNormal(normalCopy);
            parsedModel.addIndexedVertex(new IndexedVertex(posSize, 0, 0));
            originalCopy.copiedVertexCount++;
        }
        // Then duplicate the meshes using the duplicated IndexedVertices and add them to the model
        final int meshCount = originalCopy.getMeshes().size();
        for (int i = 0; i < meshCount; i++) {
            IndexedMesh reference = originalCopy.getMeshes().get(i);
            List<Integer> referenceIndices = reference.getIndices();
            IndexedMesh copy = new IndexedMesh();
            for (Integer referenceIndex : referenceIndices) {
                copy.addIndex(referenceIndex + originalCopy.copiedVertexCount);
            }
            parsedModel.addMesh(copy);
        }
    }

    @Override
    public void apply(IndexedModel model) {

    }

    @Override
    public void apply(IndexedModel model, IndexedMesh mesh) {

    }

    public static class OriginalCopy {
        public final List<Vector3f> positions;
        public final List<Vector3f> normals;
        public final List<Vector2f> uvs;
        public final List<IndexedMesh> meshes;
        public final List<IndexedVertex> indexedVertices;
        public int copiedVertexCount;

        public OriginalCopy(ObjParser.Builder model) {
            this(model.getPositions(), model.getNormals(), model.getUvs(), model.getMeshes(), model.getIndexedVertices());
        }

        public OriginalCopy(List<Vector3f> positions, List<Vector3f> normals, List<Vector2f> uvs, List<IndexedMesh> meshes, List<IndexedVertex> indexedVertices) {
            this.positions = positions.stream().map(Vector3f::new).toList();
            this.normals = normals.stream().map(Vector3f::new).toList();
            this.uvs = uvs.stream().map(Vector2f::new).toList();
            this.meshes = meshes.stream().map(original -> {
                IndexedMesh mesh = new IndexedMesh();
                mesh.setIndices(original.getIndices().stream().toList());
                return mesh;
            }).toList();
            this.indexedVertices = indexedVertices.stream().map(original -> new IndexedVertex(original.positionIndex(), original.uvIndex(), original.normalIndex())).toList();
        }

        public List<Vector3f> getPositions() {
            return positions;
        }

        public List<Vector3f> getNormals() {
            return normals;
        }

        public List<Vector2f> getUvs() {
            return uvs;
        }

        public List<IndexedMesh> getMeshes() {
            return meshes;
        }

        public List<IndexedVertex> getIndexedVertices() {
            return indexedVertices;
        }
    }
}
