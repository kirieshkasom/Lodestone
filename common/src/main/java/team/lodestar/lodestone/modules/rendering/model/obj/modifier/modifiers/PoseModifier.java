package team.lodestar.lodestone.modules.rendering.model.obj.modifier.modifiers;

import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Vector3f;
import org.joml.Vector4f;
import team.lodestar.lodestone.modules.rendering.model.obj.IndexedModel;
import team.lodestar.lodestone.modules.rendering.model.obj.ObjParser;
import team.lodestar.lodestone.modules.rendering.model.obj.data.IndexedMesh;
import team.lodestar.lodestone.modules.rendering.model.obj.modifier.ModelModifier;

import java.util.function.Consumer;

public class PoseModifier extends ModelModifier {

    private final Consumer<PoseStack> poseStackConsumer;

    public PoseModifier(Consumer<PoseStack> poseConsumer) {
        this.poseStackConsumer = poseConsumer;
    }

    @Override
    public void applyEarly(ObjParser.Builder parsedModel) {
        PoseStack poseStack = new PoseStack();
        poseStackConsumer.accept(poseStack);
        PoseStack.Pose pose = poseStack.last();

        for (Vector3f position : parsedModel.getPositions()) {
            Vector3f posCopy = new Vector3f(position);
            Vector4f posCopy4 = new Vector4f(posCopy, 1);
            posCopy4.mul(pose.pose());
            posCopy.set(posCopy4.x(), posCopy4.y(), posCopy4.z());
            position.set(posCopy);
        }

        for (Vector3f normal : parsedModel.getNormals()) {
            Vector3f normalCopy = new Vector3f(normal);
            normalCopy.mul(pose.normal());
            normal.set(normalCopy.x(), normalCopy.y(), normalCopy.z());
        }
    }

    @Override
    public void apply(IndexedModel model) {

    }

    @Override
    public void apply(IndexedModel model, IndexedMesh mesh) {

    }
}
